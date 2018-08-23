package com.kurtomerfaruk.documentwiewer;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author Omer Faruk Kurt
 * @Created on date 22/08/2018 22:26:23
 */
@ManagedBean(name = "basicDocumentViewerController")
@SessionScoped
public class BasicDocumentViewerController implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private StreamedContent content;


    public StreamedContent getContent() {
        if(content == null){
            content=pdfDocumentGenerate();
        }
        return content;
    }

    public void setContent(StreamedContent content) {
        this.content = content;
    }

  
    public DefaultStreamedContent pdfDocumentGenerate() {
        JasperPrint jasperPrint;

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String reportPath = facesContext.getExternalContext().getRealPath("/reports") + File.separator;
            String fileName = "actor_list.jasper";
            fileName = reportPath + fileName;
            Context initialContext = new InitialContext();
            
            DataSource datasource = (DataSource) initialContext.lookup("Sakila");
            Connection conn = datasource.getConnection();
            HashMap parameterMap = new HashMap();
            jasperPrint = JasperFillManager.fillReport(fileName, parameterMap, conn);

            byte[] document = JasperExportManager.exportReportToPdf(jasperPrint);
            return new DefaultStreamedContent(new ByteArrayInputStream(document), "application/pdf", "Actor_List");

        } catch (JRException | NamingException ex) {
            Logger.getLogger(BasicDocumentViewerController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SQLException ex) {
            System.out.println("No se puede obtener la conexion: " + ex);
            return null;
        }
    }

}
