/*
 * This file is part of ViaLoadingBase - https://github.com/FlorianMichael/ViaLoadingBase
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.diaoling.client.viaversion.vialoadingbase.netty;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.diaoling.client.viaversion.vialoadingbase.netty.event.CompressionReorderEvent;
import com.diaoling.client.viaversion.vialoadingbase.netty.handler.VLBViaDecodeHandler;
import com.diaoling.client.viaversion.vialoadingbase.netty.handler.VLBViaEncodeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class VLBPipeline extends ChannelInboundHandlerAdapter {
    public static final String VIA_DECODER_HANDLER_NAME = "via-decoder";
    public static final String VIA_ENCODER_HANDLER_NAME = "via-encoder";

    private final UserConnection user;

    public VLBPipeline(final UserConnection user) {
        this.user = user;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ctx.pipeline().addBefore(getDecoderHandlerName(), VIA_DECODER_HANDLER_NAME, createVLBViaDecodeHandler());
        ctx.pipeline().addBefore(getEncoderHandlerName(), VIA_ENCODER_HANDLER_NAME, createVLBViaEncodeHandler());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof CompressionReorderEvent) {
            reorderHandlersBasedOnCompression(ctx);
        }
    }

    private void reorderHandlersBasedOnCompression(ChannelHandlerContext ctx) {
        final int decoderIndex = ctx.pipeline().names().indexOf(getDecompressionHandlerName());
        if (decoderIndex != -1 && decoderIndex > ctx.pipeline().names().indexOf(VIA_DECODER_HANDLER_NAME)) {
            moveHandlersAfterCompressionHandlers(ctx);
        }
    }

    private void moveHandlersAfterCompressionHandlers(ChannelHandlerContext ctx) {
        ChannelHandler decoder = ctx.pipeline().get(VIA_DECODER_HANDLER_NAME);
        ChannelHandler encoder = ctx.pipeline().get(VIA_ENCODER_HANDLER_NAME);

        ctx.pipeline().remove(decoder);
        ctx.pipeline().remove(encoder);

        ctx.pipeline().addAfter(getDecompressionHandlerName(), VIA_DECODER_HANDLER_NAME, decoder);
        ctx.pipeline().addAfter(getCompressionHandlerName(), VIA_ENCODER_HANDLER_NAME, encoder);
    }

    protected VLBViaDecodeHandler createVLBViaDecodeHandler() {
        return new VLBViaDecodeHandler(user);
    }

    protected VLBViaEncodeHandler createVLBViaEncodeHandler() {
        return new VLBViaEncodeHandler(user);
    }

    public abstract String getDecoderHandlerName();

    public abstract String getEncoderHandlerName();

    public abstract String getDecompressionHandlerName();

    public abstract String getCompressionHandlerName();

    public UserConnection getUser() {
        return user;
    }
}