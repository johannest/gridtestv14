package com.example.application.views.test;

import com.example.application.data.entity.SamplePerson;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.*;

@PageTitle("Test-View")
@Route(value = "test-view", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class TestView extends Div {

    private int idSequence = 0;

    public TestView() {
        Grid<SamplePerson> grid = new Grid<>();
        grid.setItems(getItems());
        Grid.Column<SamplePerson> firstNameColumn = grid.addColumn(SamplePerson::getFirstName)
                .setHeader("First Name");
        grid.setPageSize(50);

        Binder<SamplePerson> binder = new Binder<>(SamplePerson.class);
        Editor<SamplePerson> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField firstNameField = new TextField();
        binder.forField(firstNameField).bind("firstName");
        firstNameColumn.setEditorComponent(firstNameField);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());
        Grid.Column<SamplePerson> editorColumn = grid.addComponentColumn(person -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(person);
                firstNameField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");
        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        Button add = new Button("Add", e-> {
            SamplePerson newPerson = new SamplePerson();
            newPerson.setId(idSequence++);
            ListDataProvider<SamplePerson> dataProvider = (ListDataProvider<SamplePerson>) grid.getDataProvider();
            dataProvider.getItems().add( newPerson );
            dataProvider.refreshAll();
            // workaround to refresh the keyMapper
            grid.getDataCommunicator().getKeyMapper().key(newPerson);
            grid.getEditor().editItem(newPerson);
        });
        add(grid, add);
    }

    private List<SamplePerson> getItems() {
        List<SamplePerson> personList = new ArrayList<>();
        for (int i=0; i<50; i++) {
            SamplePerson p = new SamplePerson();
            p.setId(idSequence++);
            p.setFirstName("Test"+i);
            personList.add(p);
        }
        return personList;
    }
}
