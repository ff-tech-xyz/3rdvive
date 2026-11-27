package com.thirdvive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.gameplay.VRPlayer;

/**
 * Where the third-person camera is, and how to move things "with" it.
 *
 * Think of the camera as a copy of your real head placed a few blocks behind (or in front of) your character, the
 * same distance vanilla third person uses. Anything
 * near your real head, like your eyes or your hands, can be moved the same way, so it keeps the same spot
 * relative to the camera that it really has relative to your head. The eyes use this to draw the view, and
 * menus, the HUD and the menu hands use it so they sit in front of the camera instead of at your character.
 *
 * Works for any {@link VRData}: world copies (blocks) and the room copy (meters) used by menu placement.
 */
public final class ThirdViveCamera {
    private static long frame = Long.MIN_VALUE;
    private static VRData frameData;
    private static boolean frameFront;
    private static float frameDistance; // world blocks, after wall clipping
    private static float frameScale = 1.0F;
    private static float partialTick = 1.0F;

    private ThirdViveCamera() {}

    private static VRData worldData() {
        VRPlayer vr = ClientDataHolderVR.getInstance().vrPlayer;
        return vr.vrdata_world_render != null ? vr.vrdata_world_render : vr.vrdata_world_pre;
    }

    /** Called at the start of each frame, so the camera follows the smoothed character position. */
    public static void setPartialTick(float tick) { partialTick = tick; }

    /** Head center: a point 0.1 m behind the headset, the pivot your eyes turn around. */
    public static Vec3 headCenter(VRData data) {
        Vector3f back = data.hmd.getMatrix().transformDirection(new Vector3f(0, 0, 0.1F * data.worldScale));
        return data.hmd.getPosition().add(back.x, back.y, back.z);
    }

    /**
     * The point the camera swings around: the character's eyes, like vanilla third person, in {@code data}'s
     * own units. With lockMovement your head is already pinned there, so the head itself is used. Without it
     * your real head can be a little away from the character (roomscale, walls); the camera stays on the character.
     */
    public static Vec3 pivot(VRData data) {
        Vec3 head = headCenter(data);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || ThirdViveState.movementLocked()) return head;
        VRData world = worldData();
        Vec3 eye = world == ClientDataHolderVR.getInstance().vrPlayer.vrdata_world_render
            ? player.getEyePosition(partialTick) : player.getEyePosition();
        Vec3 worldHead = ThirdViveCamera.headCenter(world);
        // head -> eye in world blocks, turned into real meters (room), then into this data's units
        Vector3f offset = new Vector3f((float) (eye.x - worldHead.x), (float) (eye.y - worldHead.y),
            (float) (eye.z - worldHead.z))
            .rotateY(-world.rotation_radians).div(world.worldScale)
            .rotateY(data.rotation_radians).mul(data.worldScale);
        return head.add(offset.x, offset.y, offset.z);
    }

    /** Camera distance from the pivot (vanilla's, including scale and vehicle), in {@code data}'s own units. Walls are checked once per frame. */
    public static float distance(VRData data) {
        long currentFrame = ClientDataHolderVR.getInstance().frameIndex;
        VRData world = worldData();
        boolean front = ThirdViveState.front();
        if (currentFrame != frame || world != frameData || front != frameFront) {
            Minecraft mc = Minecraft.getInstance();
            float wanted = VanillaThirdPerson.distance(mc.player);
            frameDistance = VanillaThirdPerson.maxZoom(mc.level, mc.player, pivot(world),
                direction(world, front), wanted);
            frameScale = world.worldScale;
            frame = currentFrame;
            frameData = world;
            frameFront = front;
        }
        // blocks -> real meters -> this data's units
        return frameDistance / frameScale * data.worldScale;
    }

    private static Vec3 direction(VRData data, boolean front) {
        Vec3 look = new Vec3(data.hmd.getDirection()).normalize();
        return front ? look : look.scale(-1);
    }

    /** The turn applied to everything seen from the camera: half a turn around your head's up in front view. */
    public static Matrix4f flip(VRData data) {
        if (!ThirdViveState.front()) return new Matrix4f();
        Vector3f up = data.hmd.getMatrix().transformDirection(new Vector3f(0, 1, 0)).normalize();
        return new Matrix4f().rotation((float) Math.PI, up);
    }

    /** Where the real headset point ends up: the camera center. */
    public static Vec3 center(VRData data) {
        return pivot(data).add(direction(data, ThirdViveState.front()).scale(distance(data)));
    }

    /** Moves a point near the head the same way the camera moves the head. */
    public static Vec3 shiftPoint(VRData data, Vec3 center, Matrix4f flip, Vec3 point) {
        Vec3 hmd = data.hmd.getPosition();
        Vector3f offset = flip.transformDirection(new Vector3f(
            (float) (point.x - hmd.x), (float) (point.y - hmd.y), (float) (point.z - hmd.z)));
        return center.add(offset.x, offset.y, offset.z);
    }

    /** A device (eye, hand) moved along with the camera. */
    public static VRData.VRDevicePose shift(VRData data, VRData.VRDevicePose device) {
        Matrix4f flip = flip(data);
        Vec3 world = shiftPoint(data, center(data), flip, device.getPosition());
        Matrix4f worldRot = new Matrix4f(flip).mul(device.getMatrix());
        Vector3f worldDir = flip.transformDirection(device.getDirection());
        return pose(data, world, worldRot, worldDir);
    }

    /** The camera itself, as if it were the headset. */
    public static VRData.VRDevicePose camera(VRData data) {
        return shift(data, data.hmd);
    }

    /** The camera's turn in room space (what Vivecraft keeps in {@code MCVR.hmdRotation} for the real head). */
    public static Matrix4f roomMatrix(VRData data, VRData.VRDevicePose pose) {
        return new Matrix4f().rotationY(-data.rotation_radians).mul(pose.getMatrix());
    }

    /** VRDevicePose stores room coordinates and converts them on every read, so convert the world pose back. */
    private static VRData.VRDevicePose pose(VRData data, Vec3 world, Matrix4f worldRot, Vector3f worldDir) {
        float rotation = data.rotation_radians;
        Vector3f roomPos = new Vector3f((float) (world.x - data.origin.x),
            (float) (world.y - data.origin.y), (float) (world.z - data.origin.z))
            .rotateY(-rotation).div(data.worldScale);
        Matrix4f roomRot = new Matrix4f().rotationY(-rotation).mul(worldRot);
        Vector3f roomDir = new Vector3f(worldDir).rotateY(-rotation);
        return data.new VRDevicePose(data, roomRot, roomPos, roomDir);
    }
}
