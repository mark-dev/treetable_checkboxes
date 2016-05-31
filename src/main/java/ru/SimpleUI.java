package ru;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import javax.inject.Inject;

/**
 * Created by mark on 31.05.2016.
 */
@Theme("MyApplication")
public class SimpleUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        System.out.println("Init called!");
        Navigator navigator = new Navigator(this, this);
        navigator.addView("sample", new ViewSample());
        navigator.navigateTo("sample");

    }
}
