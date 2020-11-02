//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.grpc.netty.shaded.io.netty.util;

import io.grpc.netty.shaded.io.netty.util.internal.ObjectUtil;
import io.grpc.netty.shaded.io.netty.util.internal.SystemPropertyUtil;

import java.util.Locale;

public final class NettyRuntime {
    private static final NettyRuntime.AvailableProcessorsHolder holder = new NettyRuntime.AvailableProcessorsHolder();

    private NettyRuntime() {
    }

    public static void setAvailableProcessors(int availableProcessors) {
        holder.setAvailableProcessors(availableProcessors);
    }

    public static int availableProcessors() {
        return holder.availableProcessors();
    }

    static class AvailableProcessorsHolder {
        private int availableProcessors;

        AvailableProcessorsHolder() {
        }

        synchronized void setAvailableProcessors(int availableProcessors) {
            ObjectUtil.checkPositive(availableProcessors, "availableProcessors");
            if (this.availableProcessors != 0) {
                String message = String.format(Locale.ROOT, "availableProcessors is already set to [%d], rejecting [%d]", new Object[]{Integer.valueOf(this.availableProcessors), Integer.valueOf(availableProcessors)});
                throw new IllegalStateException(message);
            } else {
                this.availableProcessors = availableProcessors;
            }
        }

        @SuppressForbidden(
                reason = "to obtain default number of available processors"
        )
        synchronized int availableProcessors() {
            if (this.availableProcessors == 0) {
                int availableProcessors = SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.availableProcessors", Runtime.getRuntime().availableProcessors());
                if (availableProcessors >= 50) {
                    availableProcessors = 50;
                }
                this.setAvailableProcessors(availableProcessors);
            }
            return this.availableProcessors;
        }
    }
}
