# treetable_checkboxes
Example of cascade selection (multiselect mode based on checkbox in each row) using vaadin treetable and custom hierarchical container
![](/img/sample.png?raw=true)

```java
   MultiSelectHierarchyContainer hc = new MultiSelectHierarchyContainer();
   //Item creation
        for (int i = 0; i < 10; i++) {
            Object itemId = i;
            Item item = hc.addItem(itemId);
            //Call this function manually for all new items
            hc.addCheckBoxColumn(itemId, String.valueOf(i), icons[rnd.nextInt(icons.length)]);
       
        //Init your custom properties like item.getItemProperty("amount").setValue(42);
      
        }
        hc.addSelectionChangedEventListener(event -> {
           //event.getSelection is Set<Item>
            Notification.show("New Selection: " + event.getSelection());
        });
        TreeTable table = new TreeTable("Table", hc);
        table.setHierarchyColumn(MultiSelectHierarchyContainer.CHECKBOX_COLUMN);
        table.setSelectable(false);
        //Dont forget yo add your columns here
        table.setVisibleColumns(MultiSelectHierarchyContainer.CHECKBOX_COLUMN);
```
check complete sample [a here](/src/main/java/ru/ViewSample.java)  

TODO:  

implement inderetminate state (currently showed as "checked")
