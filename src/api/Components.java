package api;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query-based  system for finding a Component based on its attributes.
 * Usage is the same as any other query.
 * Supports infinite widget depth.
 * <p/>
 * select() performance is typically 18-20k components in ~125ms
 * select(int widget) is dependent on how many child components are in the widget
 *
 * @author Coma
 */
public class Components extends AbstractQuery<Components, Component, ClientContext> {
    public Components(ClientContext ctx) {
        super(ctx);
    }

    @Override
    protected Components getThis() {
        return this;
    }

    /**
     * Resets and populates the query with the child components of a specific widget
     *
     * @param widget the widget to populate the query with
     * @return query populated with child components of the widget
     */
    public Components select(final int widget) {
        return select(get(widget));
    }

    public Components select(final int widget, final int subwidget) {
        return select(get(widget, subwidget));
    }

    @Override
    protected List<Component> get() {
        final List<Component> components = new ArrayList<Component>();
        for (Widget w : ctx.widgets.select()) {
            Collections.addAll(components, w.components());
        }

        return get(components);
    }

    private List<Component> get(final int widget, final int subwidget) {
        return get(ctx.widgets.widget(widget).component(subwidget).components());
    }

    private List<Component> get(final int widget) {
        return get(ctx.widgets.widget(widget).components());
    }

    private List<Component> get(final Component[] start) {
        return get(Arrays.asList(start));
    }

    private List<Component> get(final List<Component> start) {
        LinkedList<Component> widgets = new LinkedList<Component>();
        List<Component> components = new ArrayList<Component>();
        widgets.addAll(start);
        while (!widgets.isEmpty()) {
            Component c = widgets.poll();
            if (c.components().length > 0) {
                Collections.addAll(widgets, c.components());
            } else {
                components.add(c);
            }
        }
        return components;
    }

    @Override
    public Component nil() {
        return ctx.widgets.component(0, 0);
    }

    /**
     * Filters the current query's contents to only include components who match the specified widget index
     *
     * @param index index to find
     * @return filtered query
     */
    public Components widget(final int index) {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                return component.widget().id() == index;
            }
        });
    }

    /**
     * Filters the current query's contents to only include components that are currently visible
     *
     * @return filtered query
     */
    public Components visible() {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                return component.visible();
            }
        });
    }

    /**
     * Filters the current query's contents to only include components that are in the viewport
     *
     * @return filtered query
     */
    public Components inViewport() {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                return component.inViewport();
            }
        });
    }

    /**
     * Filters the current query's contents to only include components whose text matches at least one (1) of the parameter strings.
     * Comparisons are NOT case sensitive.
     *
     * @param text [varargs] the strings to compare the component text to
     * @return filtered query
     */
    public Components text(final String... text) {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                for (String s : text) {
                    if (component.text().toLowerCase().contains(s.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Filters the current query's contents to only include components whose text matches the parameter Pattern
     *
     * @param pattern the Pattern to compare component text to
     * @return filtered query
     */
    public Components text(final Pattern pattern) {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                return pattern.matcher(component.text()).find();
            }
        });
    }

    /**
     * Filters the current query's contents to only include components whose texture id matches at least one (1) of the parameter ints
     *
     * @param texture the texture ids to compare component to
     * @return filtered query
     */
    public Components texture(final int... texture) {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                for (int i : texture) {
                    if (component.textureId() == i) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Filters the current query's contents to only include components whose item ID matches at least one (1) of the parameter ints
     *
     * @param ids the item ids to compare component to
     * @return filtered query
     */
    public Components itemId(final int... ids) {
        return select(new Filter<Component>() {
            @Override
            public boolean accept(Component component) {
                for (int i : ids) {
                    if (i == component.itemId()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static Polygon RectangleToPolygon(Component c) {
        Rectangle rect = c.boundingRect();
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};
        return new Polygon(xpoints, ypoints, 4);
    }
    public static Polygon RectangleToPolygon(Rectangle rect) {
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};
        return new Polygon(xpoints, ypoints, 4);
    }
}