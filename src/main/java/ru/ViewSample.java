package ru;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;


import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Created by mark on 31.05.2016.
 */
public class ViewSample extends VerticalLayout implements View {


    public ViewSample() {
        setSizeFull();

        MultiSelectHierarchyContainer container = getContainer();

        container.addSelectionChangedEventListener(event -> {
            Notification.show("New Selection: " + event.getSelection().stream().map((i -> i.getItemProperty("amount").getValue())).collect(Collectors.toList()), Notification.Type.TRAY_NOTIFICATION);
        });

        TreeTable table = new TreeTable("Table", container);
        table.setSizeFull();
        table.setHierarchyColumn(MultiSelectHierarchyContainer.CHECKBOX_COLUMN);
        table.setSelectable(false);
        table.setVisibleColumns(MultiSelectHierarchyContainer.CHECKBOX_COLUMN, "amount", "icon");

        //You can toggle selection manually, row click for example
        table.addItemClickListener((ItemClickEvent.ItemClickListener) event -> {
            container.toggleSelection(event.getItemId());
        });

        Panel pane = new Panel(table);
        pane.setSizeFull();
        addComponent(pane);
        setExpandRatio(pane, 1);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }


    public MultiSelectHierarchyContainer getContainer() {
        Random rnd = new Random();

        MultiSelectHierarchyContainer hc = new MultiSelectHierarchyContainer();

        hc.addContainerProperty("amount", Integer.class, 0);
        hc.addContainerProperty("icon", HorizontalLayout.class, null);


        FontAwesome[] icons = FontAwesome.values();

        //Generates random Hierarchy
        for (int i = 0; i < 10; i++) {
            Object itemId = i;
            Item item = hc.addItem(itemId);
            hc.addCheckBoxColumn(itemId, String.valueOf(i), icons[rnd.nextInt(icons.length)]);

            Label label = new Label();
            label.setIcon(icons[rnd.nextInt(icons.length)]);
            label.setCaption("");
            HorizontalLayout layout = new HorizontalLayout(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

            item.getItemProperty("icon").setValue(layout);
            item.getItemProperty("amount").setValue(rnd.nextInt(255));

            if (i != 0) {
                hc.setParent(itemId, rnd.nextInt(i));
            }

        }
        return hc;
    }

}
