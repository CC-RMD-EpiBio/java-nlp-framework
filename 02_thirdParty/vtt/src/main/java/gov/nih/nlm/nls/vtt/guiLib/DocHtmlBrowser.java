package gov.nih.nlm.nls.vtt.guiLib;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.html.*;
import java.util.*;
/*****************************************************************************
* This class defines the Html-Browser class to browse HTML files.
* 
* <p><b>History:</b>
* <ul>
* </ul>
* 
* @author NLM NLS Development Team, clu
*
* @version    V-2010
*****************************************************************************/
public class DocHtmlBrowser extends JDialog implements ActionListener
{
	// public constructor
	/**
    * Create a DocHtmlBrowser object to browse HTML files by specifying owner 
	* title, and url.
    *
    * @param owner  the owner of this DocHtmlBrowser
    * @param title  the title of DocHtmlBrowser
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public DocHtmlBrowser(JFrame owner, String title, String url)
    {
        super(owner, title, true);
        setLocationRelativeTo(owner);
        setSize(500, 700);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // get html content panel
        try
        {
            htmlPane_ = new JEditorPane(url);
            urlHistory_.add(url);
            urlIndex_++;
            homeUrl_ = url;
        }
        catch(IOException e)
        {
            e.printStackTrace(System.err);
        }
        htmlPane_.setEditable(false);
        scrollPane_ = new JScrollPane(htmlPane_);
        // control panel
        JPanel controlP = new JPanel();
        controlP.add(backB_);
        controlP.add(forwardB_);
        controlP.add(reloadB_);
        controlP.add(homeB_);
        controlP.add(closeB_);
        // add Action listener
        backB_.addActionListener(this);
        forwardB_.addActionListener(this);
        reloadB_.addActionListener(this);
        homeB_.addActionListener(this);
        closeB_.addActionListener(this);
        // main panel
        mainPane_ = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        mainPane_.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 100;// resize weight: x size change 100%
        gbc.weighty = 0;  // resize weight: y size change 0% (not change)
        gbc.insets = new Insets(1, 5, 1, 5);   //Pad: Top, Left, Bottom, Right
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        GridBag.setPosSize(gbc, 0, 0, 1, 1);
        mainPane_.add(controlP, gbc);
        gbc.weighty = 100;  // resize weight: y size change 0% (not change)
        gbc.insets = new Insets(3, 5, 3, 5);   //Pad: Top, Left, Bottom, Right
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        GridBag.setPosSize(gbc, 0, 1, 1, 1);
        mainPane_.add(scrollPane_, gbc);
        getContentPane().add(mainPane_);
        activeHyperLink();
    }
	/**
    * Create a DocHtmlBrowser object to browse HTML files by specifying owner 
	* title, home, and url.
    *
    * @param owner  the owner of this DocHtmlBrowser
    * @param title  the title of DocHtmlBrowser
    * @param home  the home url of DocHtmlBrowser
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public DocHtmlBrowser(JFrame owner, String title, String home, String url)
    {
        this(owner, title, url);
        homeUrl_ = home;
    }
	/**
    * Create a DocHtmlBrowser object to browse HTML files by specifying owner 
	* title, width, height, and url.
    *
    * @param owner  the owner of this DocHtmlBrowser
    * @param title  the title of DocHtmlBrowser
    * @param width  the initial window width of DocHtmlBrowser
    * @param height  the initial window height of DocHtmlBrowser
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public DocHtmlBrowser(JFrame owner, String title, int width, int height, 
        String url)
    {
        this(owner, title, url);
        setSize(width, height);
    }
	/**
    * Create a DocHtmlBrowser object to browse HTML files by specifying owner 
	* title, width, height, home, and url.
    *
    * @param owner  the owner of this DocHtmlBrowser
    * @param title  the title of DocHtmlBrowser
    * @param width  the initial window width of DocHtmlBrowser
    * @param height  the initial window height of DocHtmlBrowser
    * @param home  the home url of DocHtmlBrowser
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public DocHtmlBrowser(JFrame owner, String title, int width, int height, 
        String home, String url)
    {
        this(owner, title, width, height, url);
        homeUrl_ = home;
    }
	/**
    * Create a DocHtmlBrowser object to browse HTML files by specifying owner 
	* title, xPos, yPos, width, height, home, and url.
    *
    * @param owner  the owner of this DocHtmlBrowser
    * @param title  the title of DocHtmlBrowser
    * @param xPos  the initial window x position of DocHtmlBrowser
    * @param yPos  the initial window y position of DocHtmlBrowser
    * @param width  the initial window width of DocHtmlBrowser
    * @param height  the initial window height of DocHtmlBrowser
    * @param home  the home url of DocHtmlBrowser
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public DocHtmlBrowser(JFrame owner, String title, int xPos, int yPos, 
		int width, int height, String home, String url)
    {
        this(owner, title, width, height, url);
		setLocation(xPos, yPos);
        homeUrl_ = home;
    }
	/**
    * Set the browsing page in DocHtmlBrowser by specifying url.
    *
    * @param url  the url of DocHtmlBrowser browse HTML files
    */
    public void setPage(String url)
    {
        goToUrl(url);
        
        urlIndex_ = 0;
        urlHistory_.clear();
        urlHistory_.add(url);
    }
	/**
    * Set the home in DocHtmlBrowser.
    *
    * @param home  home in DocHtmlBrowser
    */
    public void setHome(String home)
    {
        homeUrl_ = home;
    }
	/**
    * Get the HTML panel in DocHtmlBrowser.
    *
    * @return  HTML panel in DocHtmlBrowser
    */
    public JEditorPane getHtmlPane()
    {
        return htmlPane_;
    }
	/**
    * Get the main panel in DocHtmlBrowser.
    *
    * @return  main panel in DocHtmlBrowser
    */
    public JPanel getMainPane()
    {
        return mainPane_;
    }
	/**
    * Override method from ActionListener
    *
    * @param  evt Java ActionEvent object
    */
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();
        if(source == backB_)
        {
            if(urlIndex_ > 0)
            {
                String url = urlHistory_.get(urlIndex_-1);
                goToUrl(url);
                urlIndex_--;
            }
        }
        else if(source == forwardB_)
        {
            if((urlIndex_+1) < urlHistory_.size())
            {
                String url = urlHistory_.get(urlIndex_+1);
                goToUrl(url);
                urlIndex_++;
            }
        }
        else if(source == reloadB_)
        {
            String url = urlHistory_.get(urlIndex_);
            goToUrl(url);
        }
        else if(source == homeB_)
        {
            goToUrl(homeUrl_);
            urlHistory_.add(homeUrl_);
            urlIndex_++;
        }
        else if(source == closeB_)
        {
            this.setVisible(false);
			this.dispose();
        }
    }
	/**
    * Test driver for DocHtmlBrowser class.
    *
    * @param  args for the test driver
    */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        // Frame
        frame.setTitle("Html Browser");
        DocHtmlBrowser browser = new DocHtmlBrowser(
        frame, "Document Title", 500, 700,
        "http://lexlx1.nlm.nih.gov/index.html",
        "http://lexlx1.nlm.nih.gov/LexSysGroup/Projects/lvg/current/");
        browser.setVisible(true);
    }
    
    // private method
    private void activeHyperLink()
    {
        // add a hyperlink listener
        htmlPane_.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent ev)
            {
                try
                {
                    if(ev.getEventType() == 
                        HyperlinkEvent.EventType.ACTIVATED)
                    {
                        htmlPane_.setPage(ev.getURL());
                        // update urlHistory_
                        int size = urlHistory_.size();
                        if((urlIndex_+1) < size)
                        {
                            for(int i = (urlIndex_+1); i < size; i++)
                            {
                                urlHistory_.removeLast();
                            }
                        }
                        urlIndex_++;
                        urlHistory_.add(ev.getURL().toString());
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }
            }
        });
    }
    private void goToUrl(String url)
    {
        try
        {
            htmlPane_.setPage(url);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
    private JEditorPane htmlPane_ = null;
    private JScrollPane scrollPane_ = null;
    private JPanel mainPane_ = null;
    private JButton backB_ = new JButton("Back");
    private JButton forwardB_ = new JButton("Forward");
    private JButton reloadB_ = new JButton("Reload");
    private JButton homeB_ = new JButton("Home");
    private JButton closeB_ = new JButton("Close");
    private LinkedList<String> urlHistory_ = new LinkedList<String>();
    private int urlIndex_ = -1;
    private String homeUrl_ = null;
    private final static long serialVersionUID = 5L;
}
