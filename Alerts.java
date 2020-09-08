import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLDocument;


public class Alerts {

	private JFrame window = new JFrame("Alerts");
	
	private JButton exit    = new JButton("Exit"),
			        perform = new JButton("Perform");
	
	private JCheckBox rs22 = new JCheckBox("RS22"), 
	                  rs23 = new JCheckBox("RS23"), 
	                  rs25 = new JCheckBox("RS25"), 
	                  rs27 = new JCheckBox("RS27"), 
	                  ams_03 = new JCheckBox("AMS-VM-CQM03"),
	                  wal_01 = new JCheckBox("WAL-VM-CQM01"),
	                  ams_04 = new JCheckBox("AMS-VM-CQM04"), 
	                  nwah2 = new JCheckBox("NWA-VM-H2W7603"), 
	                  nwa_qm = new JCheckBox("NWA-VM-QM01"), 
	                  nwt_kj = new JCheckBox("NWT-VM-KJWIN764"), 
	                  wal = new JCheckBox("WAL-VM-W12R2");
	
	JTextPane logArea = new JTextPane();
	JScrollPane logScrollPane = new JScrollPane(logArea);
	
	public static void main(String[] args) throws IOException 
	{
		Alerts alert = new Alerts();
	}
	
	public Alerts()
	{
		window.setSize(610, 400);
		window.setResizable(false);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		  
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		  
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			  
		}
		  
		window.setLocation(width / 3, height / 4);
		window.setContentPane(initContent());
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JPanel initContent()
	{
		JPanel main = new JPanel(new BorderLayout(5, 5));
		main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel serverGrid = new JPanel(new GridLayout(5, 2, 5, 5));
		serverGrid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		serverGrid.add(rs22);
		serverGrid.add(rs23);
		serverGrid.add(rs25);
		serverGrid.add(rs27);
		serverGrid.add(ams_03);
		serverGrid.add(wal_01);
		serverGrid.add(ams_04);
		serverGrid.add(nwah2);
		serverGrid.add(nwa_qm);
		serverGrid.add(nwt_kj);
		serverGrid.add(wal);
		
		
		logArea.setContentType("text/html");
		logArea.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		logArea.setEditable(false);
		logArea.setSize(70, 170);
		logArea.setPreferredSize(new Dimension(140, 170));
		logArea.setAutoscrolls(true);
		logArea.setFont(logArea.getFont().deriveFont(5f));
		
		JPanel buttons = new JPanel(new FlowLayout());
		perform.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				new DoPerform().start();
				
				perform.setEnabled(false);
				window.setTitle("Alerts. In Progress.....");
			}
		});
		buttons.add(perform);
		exit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		buttons.add(exit);
		
		
		
		JLabel label = new JLabel("<html><b>Set</b> checkbox if you want to enable alert collection<br><b>Uncheck</b> checkbox if you want to disable alert collection</html>", SwingConstants.CENTER);
		main.add(label, BorderLayout.NORTH);
		main.add(serverGrid, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.EAST);
		main.add(logScrollPane, BorderLayout.SOUTH);

		return main;
	}
	
	class DoPerform extends Thread
	{
		@Override
		public void run()
		{
			try 
			{
				goThroughServerList();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (BadLocationException e) 
			{
				e.printStackTrace();
			}
			
			EventQueue.invokeLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					perform.setEnabled(true);
					window.setTitle("Alerts");
				}
			});
		}
		
		private void goThroughServerList() throws IOException, BadLocationException
		{
			HTMLDocument doc=(HTMLDocument) logArea.getStyledDocument();
			
			doc.insertString(doc.getLength(), "----------------", null);
			doc.insertString(doc.getLength(), "\n", null);
			
			perform("RS22", "50443", rs22.isSelected());
			perform("RS23", "50443", rs23.isSelected());
			perform("RS25", "50443", rs25.isSelected());
			perform("RS27", "50443", rs27.isSelected());
			
			perform("AMS-VM-CQM03", "443", ams_03.isSelected());
			perform("WAL-VM-CQM01", "443", wal_01.isSelected());
			perform("AMS-VM-CQM04", "443", ams_04.isSelected());
			perform("NWA-VM-H2W7603", "443", nwah2.isSelected());
			perform("NWA-VM-QM01", "443", nwa_qm.isSelected());
			perform("NWT-VM-KJWIN764", "443", nwt_kj.isSelected());
			perform("WAL-VM-W12R2", "443", wal.isSelected());
			
			JOptionPane.showMessageDialog(window, "Done !");
		}
		
		private void perform(String serverURL, String port, boolean state) throws IOException, BadLocationException
		{
			String urlParameters  = "action=login&username=administrator&password=password&sysname=";
			byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
			int    postDataLength = postData.length;
			URL    url            = new URL("https://" + serverURL + ":" + port + "/webclient/login");
			
			// Create a trust manager that does not validate certificate chains
		    TrustManager[] trustAllCerts = new TrustManager[] 
		    {
		        new X509TrustManager()
		        {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
		            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
		            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
		        }
		    };
		    
		    // Install the all-trusting trust manager
		    try 
		    {
		        SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    } 
		    catch (Exception e) 
		    {
		    }
			
		    // Login process...
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			
			conn.setHostnameVerifier(new TrustAllHostNameVerifier());
			
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches(false);
			
			try
			{
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.write( postData );
				wr.flush();
				wr.close();
			}
			catch (ConnectException|SSLHandshakeException|UnknownHostException ce)
			{
				HTMLDocument doc=(HTMLDocument) logArea.getStyledDocument();
				
				doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "Looks like server " + serverURL + " is <b>down</b> <span style=\"color:red\"> <b> [FAILED] </b> </span>");
				doc.insertString(doc.getLength(), "\n", null);
				
				return;
			}
			
			
			// Get cookie...
			List<String> setCookies = conn.getHeaderFields().get("Set-Cookie");
			
			conn.disconnect();
			
			String yourIdCookie = setCookies.get(0).substring(0, setCookies.get(0).indexOf(";"));
			
			HTMLDocument doc=(HTMLDocument) logArea.getStyledDocument();
			doc.insertString(doc.getLength(), serverURL + " : " + yourIdCookie + " : ", null);
			
			// Enable / Disable collection of alerts
			url = new URL("https://" + serverURL + ":" + port + "/webclient/monitoringCfg/data/configsAll/enabled");
			
			conn = (HttpsURLConnection) url.openConnection();
			
			conn.setHostnameVerifier(new TrustAllHostNameVerifier());
			
			conn.setDoOutput(true);
		    conn.setInstanceFollowRedirects(false);
		    conn.setRequestMethod("PUT");
		    conn.setRequestProperty("Content-Type", "text/plain"); 
		    conn.setRequestProperty("Cookie", yourIdCookie);
		    conn.setUseCaches(false);
			
			
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(Boolean.toString(state));
		    out.flush();
		    out.close();
		    System.out.println(conn.getResponseCode());
		    
		    if( conn.getResponseCode() == 401 ) // 401 code - Access allowed only for registered users.
		    {
		    	doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), conn.getResponseCode() + "<span style=\"color:red\"> <b> [FAILED] </b> due to 401 http code </span>");
		    	doc.insertString(doc.getLength(), "\n", null);
		    	
		    	conn.disconnect();
		    	return;
		    }
		    
		    doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), conn.getResponseCode() + "<span style=\"color:green\"> <b> [OK] </b> </span>");
			doc.insertString(doc.getLength(), "\n", null);
			
			conn.disconnect();
		}
	}
}

// For certificate purposes
class TrustAllHostNameVerifier implements HostnameVerifier 
{
    public boolean verify(String hostname, SSLSession session) 
    {
        return true;
    }
}
