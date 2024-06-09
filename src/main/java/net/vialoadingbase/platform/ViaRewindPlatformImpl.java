package net.vialoadingbase.platform;

import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import net.vialoadingbase.ViaLoadingBase;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl implements ViaRewindPlatform {
   public ViaRewindPlatformImpl(File directory) {
      ViaRewindConfigImpl config = new ViaRewindConfigImpl(new File(directory, "viarewind.yml"));
      config.reloadConfig();
      this.init(config);
   }

   public Logger getLogger() {
      return ViaLoadingBase.LOGGER;
   }
}
