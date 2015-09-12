package ro.polak.webserver.servlet;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import ro.polak.webserver.JLWSConfig;

public class DesktopServletServiceDriver implements IServletServiceDriver {

    private Servlet littleServlet;
    // private static ServletPool servletPool = new ServletPool();
    private static ClassLoader classLoader = null;

    static {
        try {
            DesktopServletServiceDriver.classLoader = new URLClassLoader((new URL[]{new URL("file", "", new File(JLWSConfig.DocumentRoot).getCanonicalPath().replace('\\', '/') + "/")}));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads requested little servlet
     *
     * @param servletPath the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    public boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException {
        int lastSlashpos = 0;

        try {
            lastSlashpos = servletPath.lastIndexOf("/");
        } catch (Exception e) {
        }

        String servletName = servletPath.substring(lastSlashpos + 1);
        String servletDir = servletPath.substring(0, lastSlashpos + 1);

        try {
            servletName = servletName.substring(0, servletName.indexOf("."));
        } catch (Exception e) {
        }
        servletName = servletDir.substring(1).replaceAll("/", ".")
                + servletName;

        // littleServlet = ServletService.servletPool.getServlet( servletName );
        //
        //
        // if( littleServlet != null )
        // {
        // return true;
        // }

        try {
            littleServlet = (Servlet) DesktopServletServiceDriver.classLoader
                    .loadClass(servletName).newInstance();

            try {
                littleServlet.directory = (new File(JLWSConfig.DocumentRoot))
                        .getCanonicalPath();
            } catch (java.io.IOException e) {
                littleServlet.directory = JLWSConfig.DocumentRoot;
            }
        } catch (ClassCastException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }

        // ServletService.servletPool.add(servletName, littleServlet);
        littleServlet.initialize();

        return true;
    }

    /**
     * Runs (starts) servlet
     *
     * @param request  http request
     * @param response http response
     */
    public void rollServlet(HTTPRequest request, HTTPResponse response) {
        if (littleServlet == null) {
            return;
        }

        littleServlet.run(request, response);
        littleServlet = null;

        // Calling garbage collector
        System.gc();
        System.gc();
    }
}