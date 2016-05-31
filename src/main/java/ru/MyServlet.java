package ru;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by mark on 31.05.2016.
 */
@WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1, initParams = {
        //  @WebInitParam(name = "org.atmosphere.cpr.asyncSupport", value = "org.atmosphere.container.JSR356AsyncSupport")

        @WebInitParam(name = "org.atmosphere.useNative", value = "true")
})
@VaadinServletConfiguration(productionMode = true,
        ui = ru.SimpleUI.class,
        resourceCacheTime = 0)
public class MyServlet extends VaadinServlet {
}
