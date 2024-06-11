package net.viamcp;

import com.diaoling.client.viaversion.vialoadingbase.netty.VLBPipeline;
import com.viaversion.viaversion.api.connection.UserConnection;
public class MCPVLBPipeline extends VLBPipeline {
   public MCPVLBPipeline(UserConnection user) {
      super(user);
   }

   public String getDecoderHandlerName() {
      return "decoder";
   }

   public String getEncoderHandlerName() {
      return "encoder";
   }

   public String getDecompressionHandlerName() {
      return "decompress";
   }

   public String getCompressionHandlerName() {
      return "compress";
   }
}
