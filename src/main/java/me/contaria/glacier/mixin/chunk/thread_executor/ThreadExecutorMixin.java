package me.contaria.glacier.mixin.chunk.thread_executor;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * During chunk future generation, the {@link MinecraftServer} queues {@link Runnable}'s while running tasks.
 * Since we know the thread isn't parked, we can skip the call to unpark it.
 */
@Mixin(ThreadExecutor.class)
public abstract class ThreadExecutorMixin {

    @WrapWithCondition(
            method = "send(Ljava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/locks/LockSupport;unpark(Ljava/lang/Thread;)V"
            )
    )
    private boolean checkThreadBeforeUnpark(Thread thread) {
        return Thread.currentThread() != thread;
    }
}
