package br.com.caelum.stella.boleto.transformer;

import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.exception.GeracaoBoletoException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

/**
 * Gerador de boletos em HTML
 * 
 * @author Mario Amaral <a href="github.com/mariofts">Github</a>
 * 
 */
@SuppressWarnings("deprecation")
public class GeradorDeBoletoHTML extends GeradorDeBoleto {
	
	private HtmlExporter exporter = new HtmlExporter();

	{
		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, 1.3F);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "stella-boleto?image=");
		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
	}
	
	/**
	 * Contrói um gerador de boletos com o template padrão.
	 * 
	 * @param boletos boleto a serem gerados.
	 */
	public GeradorDeBoletoHTML(Boleto... boletos) {
		super(boletos);
	}
	
	/**
	 * Cria um gerador de boletos que usa um template customizado.
	 * 
	 * @param template o template (.jasper) a ser usado (obrigatório).
	 * @param parametros parametros extras para o relatório( opcional).
	 * @param boletos boletos.
	 */
	public GeradorDeBoletoHTML(InputStream template,  Map<String, Object> parametros, Boleto... boletos) {
		super(template, parametros, boletos);
	}
	
	/**
	 * Seta propriedades para o gerador de html do jasper.
	 * 
	 * @param parameter propriedade aser setada.
	 * @param value valor da propriedade.
	 */
	public void setJasperParameter(JRExporterParameter parameter, Object value){
		exporter.setParameter(parameter, value);
	}

	/**
	 * Gera um boleto em HTML, e grava no caminho informado.
	 * 
	 * @param arquivo caminho do arquivo.
	 */
	public void geraHTML(String arquivo) {
		try {
			JasperExportManager.exportReportToHtmlFile(geraRelatorio(), arquivo);
		} catch (JRException e) {
			throw new GeracaoBoletoException(e);
		}
	}

    /**
     * @return bytearray do relatório
     */
	public byte[] geraHTML() {
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(geraRelatorio()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        SimpleHtmlReportConfiguration configuration = new SimpleHtmlReportConfiguration();
		configuration.setZoomRatio(1.5F);
		configuration.setEmbedImage(true);

		exporter.setConfiguration(configuration);
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new GeracaoBoletoException(e);
        }

        return outputStream.toByteArray();
    }

	/**
	 * Gera um boleto em HTML, e grava no caminho informado.
	 * 
	 * @param arquivo caminho do arquivo.
	 */
	public void geraHTML(File arquivo) {
		geraHTML(arquivo.getAbsolutePath());
	}

	/**
	 * Gera o boleto no formato HTML, e escreve o conteúdo do HTML no Writer informado.
	 * 
	 * @param writer local para gravação do arquivo
	 * @param request requisição
	 */
	public void geraHTML(Writer writer, HttpServletRequest request) {
		try {
			HtmlExporter exporter = getHtmlExporter(request);
			exporter.setParameter(JRHtmlExporterParameter.OUTPUT_WRITER, writer);
			exporter.exportReport();	
		} catch (JRException e) {
			throw new GeracaoBoletoException(e);
		}
	}

	/**
	 * Obtém o JRExporter para HTML. 
	 * 
	 * @param request requisição.
	 * @return exporter do Jasper configurado.
	 */
	private HtmlExporter getHtmlExporter(HttpServletRequest request) {
		JasperPrint relatorio = geraRelatorio();

		exporter.setParameter(JRHtmlExporterParameter.JASPER_PRINT, relatorio);
		request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, relatorio);

		return exporter;
	}
}
