package com.vaadin.k8s.views.personas;

import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.k8s.data.entity.SamplePerson;
import com.vaadin.k8s.data.service.SamplePersonService;
import com.vaadin.k8s.views.MainLayout;

@PageTitle("Personas")
@Route(value = "personas/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
@PreserveOnRefresh
public class PersonasView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private static final long serialVersionUID = 1L;
    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "personas/%d/edit";

    private Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField occupation;
    private Checkbox important;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button plus = new Button("+");

    private BeanValidationBinder<SamplePerson> binder;

    private SamplePerson samplePerson;

    private SamplePersonService samplePersonService;

    private Div editorLayoutDiv;

    private ConfirmDialog dialog;
    private ContinueNavigationAction posponed;

    public PersonasView(@Autowired SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("personas-view", "flex", "flex-col", "h-full");

        // Create UI
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        horizontalLayout.setSizeFull();

        createGridLayout(horizontalLayout);
        createEditorLayout(horizontalLayout);
        showDetail(false);

        add(horizontalLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");
        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);

        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                closeEditor();
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SamplePerson.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // Add actions to the buttons
        plus.addClickListener(e -> populateForm(null));
        cancel.addClickListener(e -> closeEditor());
        save.addClickListener(e -> {
            try {
                if (this.samplePerson == null) {
                    this.samplePerson = new SamplePerson();
                }
                binder.writeBean(this.samplePerson);

                samplePersonService.update(this.samplePerson);
                clearForm();
                refreshGrid();
                Notification.show("SamplePerson details stored.");
                closeEditor();
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the samplePerson details.");
            }
        });

        // Configure dialog to discard unsaved changes
        dialog = new ConfirmDialog(null, "Do you want to discard your changes?", "No", e -> {
            posponed = null;
        }, "Yes", e -> {
            clearForm();
            if (posponed != null) {
                posponed.proceed();
                posponed = null;
            }
        });
    }

    @Override
    protected void onAttach(AttachEvent event) {
        UI.getCurrent().addShortcutListener(cancel::click, Key.ESCAPE);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> samplePersonId = event.getRouteParameters().getInteger(SAMPLEPERSON_ID);
        if (samplePersonId.isPresent() && !binder.hasChanges()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %d", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PersonasView.class);
            }
        } else if (!samplePersonId.isPresent()) {
            clearForm();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (binder.hasChanges()) {
            dialog.open();
            posponed = event.postpone();
        }
    }

    private void closeEditor() {
        UI.getCurrent().navigate(PersonasView.class);
    }

    private void createEditorLayout(HorizontalLayout splitLayout) {
        editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("detail flex flex-col bg-contrast-5");
        editorLayoutDiv.setMaxWidth("350px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        dateOfBirth = new DatePicker("Date Of Birth");
        occupation = new TextField("Occupation");
        important = new Checkbox("Important");
        important.getStyle().set("padding-top", "var(--lumo-space-m)");
        Component[] fields = new Component[]{firstName, lastName, email, phone, dateOfBirth, occupation, important};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);

        createButtonLayout(editorLayoutDiv);

        splitLayout.add(editorLayoutDiv);

    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(HorizontalLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.add(wrapper);
        plus.setClassName("fab-button");
        plus.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        wrapper.add(grid);
        wrapper.add(plus);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
        showDetail(false);
    }

    private void populateForm(SamplePerson value) {
        this.samplePerson = value;
        binder.readBean(this.samplePerson);
        showDetail(true);
    }

    private void showDetail(boolean show) {
        editorLayoutDiv.setVisible(show);
        plus.setVisible(!show);
    }
}