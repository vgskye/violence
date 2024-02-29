package vg.skye.violence;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Violence implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("violence");

	@Override
	public void onInitialize() {
		LOGGER.warn("None of this was a good idea. Continue at your own risk!");
	}
}