/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.Facility;
import entity.Region;
import entity.ServiceRecord;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import session.stateless.OpCrmBean;

/**
 *
 * @author user
 */
@WebServlet(name = "PrintServiceRecord", urlPatterns = {"/print_service_record", "/print_service_record?*"})
public class PrintServiceRecord extends HttpServlet {

    @Resource(name = "islandFurnitureSystemDataSource")
    private DataSource islandFurnitureSystemDataSource;
    @EJB
    private OpCrmBean ocb;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String action = request.getParameter("action");
            if (action.equals("print")) {
                exportMyEventsToPDFBySQLConn(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void exportMyEventsToPDFBySQLConn(HttpServletRequest request,
            HttpServletResponse response) {
        try {
            ServiceRecord selectedServiceRecord = (ServiceRecord) request.getSession().getAttribute("selectedServiceRecord");
             System.out.println(selectedServiceRecord.getCustName());
            String userEmail = (String) request.getSession().getAttribute("email");
            Facility userFacility = ocb.getUserFacility(userEmail);
            Region userRegion = ocb.getUserRegion(userFacility);
            System.out.println(userRegion.getName());
            //Service overallDelivery = (Service) request.getSession().getAttribute("overallDeliveryForServiceRecord");
            //System.out.println(overallDelivery.getName());
            //ocb.addOverallDelivery(overallDelivery, selectedServiceRecord);
            InputStream reportStream = getServletConfig().getServletContext().getResourceAsStream("/jasperReports/ServiceRecord.jasper");
            response.setContentType("application/pdf");
            ServletOutputStream outputStream = response.getOutputStream();
            HashMap parameters = new HashMap();
            parameters.put("ID", selectedServiceRecord.getId().toString());
            parameters.put("CustomerName", selectedServiceRecord.getCustName());
            parameters.put("Address", selectedServiceRecord.getAddress());
            parameters.put("orderTime", selectedServiceRecord.getOrderTime().toString());
            parameters.put("Store", selectedServiceRecord.getStore().getName());
            parameters.put("serviceDate", selectedServiceRecord.getSvcDate().toString());
            parameters.put("filterServiceRecordId", selectedServiceRecord.getId());
            parameters.put("regionId", userRegion.getId());
            System.out.println("REGION IN PRINT SERVICE RECORD IS: "+userRegion.getName());
            JasperRunManager.runReportToPdfStream(reportStream, outputStream,
                    parameters, islandFurnitureSystemDataSource.getConnection());
            outputStream.flush();
            outputStream.close();
        } catch (JRException jrex) {
            System.out.println("********** Jasperreport Exception");
            jrex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
