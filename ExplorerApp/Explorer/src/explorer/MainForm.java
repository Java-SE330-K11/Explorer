/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer;

import java.awt.Color;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import org.apache.commons.io.FileUtils;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.xml.bind.DatatypeConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
/**
 *
 * @author User
 */
public class MainForm extends javax.swing.JFrame {

    //github
    //khoa
    String[] tmpS;
    String[] tmpF;
    boolean openingInTable=false;
    int tableIndex=-1;
    boolean isUp=false;
    boolean isOpen=false;
    boolean isGotoAddress=false;
    boolean isCreatingNode=false;
    boolean isBacking=false;
    boolean isForwarding=false;
    boolean isSelectAll=false;
    boolean isHidden=false;
    boolean isSearching=false;
    String strCreateNode=null;
    String strBack;
    String strForward;
    
    private boolean copy = false;
    private boolean cut = false;
    private File fileCoppyPath;
    private File filePatsePath;
    private File fileEx;
    private String nameEx;
    
    private File[] ArrCoppyFile;
    private ArrayList<String> saveNode = new ArrayList<>();
    int index=-1;
    
    private boolean isRenameClick=false;
    private boolean isTaoMoiThuMuc=false;
    private boolean isTaoMoiFile=false;
    private File[] paths;
    private DefaultMutableTreeNode saveSelectedNode=null;
    private DefaultMutableTreeNode treeRoot=null;
    
    public MainForm() {
        initComponents();
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        hiddenCheck.setSelected(false);
        Tree.setCellRenderer(new TreeNodeRender());
        SetUpPopupMenus();
        jScrollPane2.getViewport().setBackground(Color.WHITE);
        
    }
    class TableRender extends DefaultTableCellRenderer{
            public TableRender() { 
            
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                String path=(String)table.getModel().getValueAt(row, column);
                //String selectedFilePath=saveSelectedNode.toString()+"\\"+path;
                
                
                this.setOpaque(true);
                File []fp=java.io.File.listRoots();
//                for(File f : fp){
//                    if(f.getAbsolutePath().equals(path))
//                        selectedFilePath=path;
//                }
                
                if(saveSelectedNode.toString().equals("ThisPC"))
                {
                    if(path.equals(fp[0].getAbsolutePath()))
                    this.setIcon(new ImageIcon(getClass().getResource("/explorer/image/oC.png")));
                    else
                        this.setIcon(new ImageIcon(getClass().getResource("/explorer/image/oK.png")));
                    
                }
                else
                this.setIcon(FileSystemView.getFileSystemView().getSystemIcon(new File(path)));
                
                this.setText(new File(path).getName());
                this.setBackground(Color.WHITE);
                if (isSelected)
                {
                    setBackground(table.getSelectionBackground());
                }
                else
                {
                    setBackground(table.getBackground());
                }
                return this;
            }
    }
    class CustomEditor extends DefaultCellEditor{

        public CustomEditor(JTextField textField) {
            super(textField);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            
            JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

            if(column!=0) return null;
            if((column==0) && (isRenameClick==false && isTaoMoiThuMuc==false && isTaoMoiFile==false)) return null;
            if (value != null)
                {
                    editor.setText(value.toString());
                }
            if(value==null)
                {
                    editor.setText("nothing");
                }   
            return editor;
        }
    }
    public class TableCellListener implements PropertyChangeListener, Runnable
    {
	private JTable table;
        private JTree tree;
	private Action action;

	private int row;
	private int column;
	private Object oldValue;
	private Object newValue;

	public TableCellListener(JTable table, JTree tree)
	{
		this.table = table;
		this.tree = tree;
		this.table.addPropertyChangeListener( this );
	}

	
	private TableCellListener(JTable table, int row, int column, Object oldValue, Object newValue)
	{
		this.table = table;
		this.row = row;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 *  Get the column that was last edited
	 *
	 *  @return the column that was edited
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 *  Get the new value in the cell
	 *
	 *  @return the new value in the cell
	 */
	public Object getNewValue()
	{
		return newValue;
	}

	/**
	 *  Get the old value of the cell
	 *
	 *  @return the old value of the cell
	 */
	public Object getOldValue()
	{
		return oldValue;
	}

	/**
	 *  Get the row that was last edited
	 *
	 *  @return the row that was edited
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 *  Get the table of the cell that was changed
	 *
	 *  @return the table of the cell that was changed
	 */
	public JTable getTable()
	{
		return table;
	}
//
//  Implement the PropertyChangeListener interface
//
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		//  A cell has started/stopped editing

		if ("tableCellEditor".equals(e.getPropertyName()))
		{
			if (table.isEditing())
				processEditingStarted();
			else
				processEditingStopped();
		}
	}

	/*
	 *  Save information of the cell about to be edited
	 */
	private void processEditingStarted()
	{
		//  The invokeLater is necessary because the editing row and editing
		//  column of the table have not been set when the "tableCellEditor"
		//  PropertyChangeEvent is fired.
		//  This results in the "run" method being invoked

		SwingUtilities.invokeLater( this );
	}
	/*
	 *  See above.
	 */
	@Override
	public void run()
	{
		row = table.convertRowIndexToModel( table.getEditingRow() );
		column = table.convertColumnIndexToModel( table.getEditingColumn() );
		oldValue = table.getModel().getValueAt(row, column);
		newValue = null;
	}

	/*
	 *	Update the Cell history when necessary
	 */
	private void processEditingStopped()
	{
		newValue = table.getModel().getValueAt(row, column);

		//  The data has changed, invoke the supplied Action

		if (! newValue.equals(oldValue))
		{
			//  Make a copy of the data in case another cell starts editing
			//  while processing this change

			TableCellListener tcl = new TableCellListener(
				getTable(), getRow(), getColumn(), getOldValue(), getNewValue());

			ActionEvent event = new ActionEvent(
				tcl,
				ActionEvent.ACTION_PERFORMED,
				"");
                        String fileOld=saveSelectedNode.toString()+"\\"+oldValue;
                        String fileNew=saveSelectedNode.toString()+"\\"+newValue;
                        File fO=new File(fileOld);
                        File fN=new File(fileNew);
                        fO.renameTo(fN);
                        loadTableWhenAction();
		}
                isRenameClick=false;
                isTaoMoiThuMuc=false;
                isTaoMoiFile=false;
	}
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        setHiddenCheck = new javax.swing.JCheckBoxMenuItem();
        popupMenuPanel = new javax.swing.JPopupMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnBack = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnUp = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        lbAddress = new javax.swing.JLabel();
        textAddress = new javax.swing.JTextField();
        btnGoto = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnCopy = new javax.swing.JButton();
        btnCut = new javax.swing.JButton();
        btnPaste = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnExtract = new javax.swing.JButton();
        jToolBar3 = new javax.swing.JToolBar();
        jTextField1 = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tree = new javax.swing.JTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        itemNew = new javax.swing.JMenu();
        itemFolder = new javax.swing.JMenuItem();
        itemFile = new javax.swing.JMenuItem();
        itemRename = new javax.swing.JMenuItem();
        itemDelete = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();
        Edit = new javax.swing.JMenu();
        itemCopy = new javax.swing.JMenuItem();
        itemCut = new javax.swing.JMenuItem();
        itemPaste = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        itemSellectAll = new javax.swing.JMenuItem();
        Help = new javax.swing.JMenu();
        itemAbout = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        hiddenCheck = new javax.swing.JCheckBoxMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();

        popupMenu.setOpaque(false);
        popupMenu.setPreferredSize(new java.awt.Dimension(200, 200));

        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem2.setText("Copy");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        popupMenu.add(jMenuItem2);

        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem3.setText("Cut");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        popupMenu.add(jMenuItem3);
        popupMenu.add(jSeparator6);

        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem4.setText("Delete");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        popupMenu.add(jMenuItem4);

        jMenuItem5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem5.setText("Rename");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        popupMenu.add(jMenuItem5);
        popupMenu.add(jSeparator7);

        jMenuItem6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem6.setText("Select All");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        popupMenu.add(jMenuItem6);
        popupMenu.add(jSeparator5);

        setHiddenCheck.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        setHiddenCheck.setSelected(true);
        setHiddenCheck.setText("Set Hidden");
        setHiddenCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setHiddenCheckActionPerformed(evt);
            }
        });
        popupMenu.add(setHiddenCheck);

        popupMenuPanel.setPreferredSize(new java.awt.Dimension(160, 130));

        jMenuItem7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem7.setText("jMenuItem7");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        popupMenuPanel.add(jMenuItem7);
        popupMenuPanel.add(jSeparator8);

        jMenuItem8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem8.setText("jMenuItem8");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        popupMenuPanel.add(jMenuItem8);
        popupMenuPanel.add(jSeparator9);

        jMenu1.setText("New..");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItem9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem9.setText("jMenuItem9");
        jMenuItem9.setPreferredSize(new java.awt.Dimension(130, 25));
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);

        jMenuItem10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem10.setText("jMenuItem10");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        popupMenuPanel.add(jMenu1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Basic Explorer");
        setBackground(new java.awt.Color(255, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jToolBar2.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar2.setRollover(true);
        jToolBar2.setEnabled(false);

        btnBack.setBackground(new java.awt.Color(255, 255, 255));
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/back.png"))); // NOI18N
        btnBack.setToolTipText("Back");
        btnBack.setFocusable(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackMouseClicked(evt);
            }
        });
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        jToolBar2.add(btnBack);

        btnForward.setBackground(new java.awt.Color(255, 255, 255));
        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/forward.png"))); // NOI18N
        btnForward.setToolTipText("Forward");
        btnForward.setFocusable(false);
        btnForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnForward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnForwardMouseClicked(evt);
            }
        });
        jToolBar2.add(btnForward);
        jToolBar2.add(jSeparator1);

        btnUp.setBackground(new java.awt.Color(255, 255, 255));
        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/up.png"))); // NOI18N
        btnUp.setToolTipText("Up");
        btnUp.setFocusable(false);
        btnUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpMouseClicked(evt);
            }
        });
        jToolBar2.add(btnUp);
        jToolBar2.add(jSeparator2);

        lbAddress.setBackground(new java.awt.Color(255, 255, 255));
        lbAddress.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lbAddress.setText("Address: ");
        jToolBar2.add(lbAddress);

        textAddress.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        textAddress.setMinimumSize(new java.awt.Dimension(600, 22));
        textAddress.setPreferredSize(new java.awt.Dimension(600, 220));
        jToolBar2.add(textAddress);

        btnGoto.setBackground(new java.awt.Color(255, 255, 255));
        btnGoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/goto.png"))); // NOI18N
        btnGoto.setText("Go to");
        btnGoto.setToolTipText("Go to address");
        btnGoto.setFocusable(false);
        btnGoto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGotoMouseClicked(evt);
            }
        });
        btnGoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGotoActionPerformed(evt);
            }
        });
        jToolBar2.add(btnGoto);

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setRollover(true);
        jToolBar1.setEnabled(false);

        btnCopy.setBackground(new java.awt.Color(255, 255, 255));
        btnCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/copy.png"))); // NOI18N
        btnCopy.setText("Copy");
        btnCopy.setToolTipText("Copy");
        btnCopy.setFocusable(false);
        btnCopy.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCopy.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCopy.setMinimumSize(new java.awt.Dimension(70, 40));
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCopy);

        btnCut.setBackground(new java.awt.Color(255, 255, 255));
        btnCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/cut.png"))); // NOI18N
        btnCut.setText("Cut");
        btnCut.setToolTipText("Cut");
        btnCut.setFocusable(false);
        btnCut.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCut.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCut.setMinimumSize(new java.awt.Dimension(70, 40));
        btnCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCut);

        btnPaste.setBackground(new java.awt.Color(255, 255, 255));
        btnPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/paste.png"))); // NOI18N
        btnPaste.setText("Paste");
        btnPaste.setToolTipText("Paste");
        btnPaste.setFocusable(false);
        btnPaste.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPaste.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnPaste.setMinimumSize(new java.awt.Dimension(70, 40));
        btnPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPaste);

        btnDelete.setBackground(new java.awt.Color(255, 255, 255));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/delete.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setToolTipText("Delete");
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnDelete.setMinimumSize(new java.awt.Dimension(70, 40));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnRefresh.setBackground(new java.awt.Color(255, 255, 255));
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/refresh.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setToolTipText("Reload data");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRefresh.setMinimumSize(new java.awt.Dimension(70, 40));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefresh);

        btnExtract.setBackground(new java.awt.Color(255, 255, 255));
        btnExtract.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/archive.png"))); // NOI18N
        btnExtract.setText("Extract");
        btnExtract.setFocusable(false);
        btnExtract.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnExtract.setMaximumSize(new java.awt.Dimension(90, 47));
        btnExtract.setMinimumSize(new java.awt.Dimension(70, 40));
        btnExtract.setName("btnExtract"); // NOI18N
        btnExtract.setPreferredSize(new java.awt.Dimension(90, 47));
        btnExtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtractActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExtract);

        jToolBar3.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar3.setRollover(true);
        jToolBar3.setMaximumSize(new java.awt.Dimension(341, 80));
        jToolBar3.setMinimumSize(new java.awt.Dimension(341, 40));
        jToolBar3.setPreferredSize(new java.awt.Dimension(100, 50));

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jTextField1.setText("Search..");
        jTextField1.setToolTipText("search..");
        jTextField1.setMaximumSize(new java.awt.Dimension(300, 30));
        jTextField1.setMinimumSize(new java.awt.Dimension(300, 30));
        jTextField1.setPreferredSize(new java.awt.Dimension(300, 30));
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextField1MouseExited(evt);
            }
        });
        jToolBar3.add(jTextField1);

        btnSearch.setBackground(new java.awt.Color(255, 255, 255));
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/search.png"))); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSearch.setMaximumSize(new java.awt.Dimension(28, 28));
        btnSearch.setMinimumSize(new java.awt.Dimension(28, 23));
        btnSearch.setPreferredSize(new java.awt.Dimension(40, 40));
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar3.add(btnSearch);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jSplitPane1.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPane1.setDividerLocation(250);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseReleased(evt);
            }
        });
        jScrollPane2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane2KeyPressed(evt);
            }
        });

        Table.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        Table.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        Table.setFocusable(false);
        Table.setGridColor(new java.awt.Color(255, 255, 255));
        Table.setRowHeight(22);
        Table.getTableHeader().setReorderingAllowed(false);
        Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TableMouseReleased(evt);
            }
        });
        Table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(Table);

        jSplitPane1.setRightComponent(jScrollPane2);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("ThisPC");
        Tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        Tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TreeMouseClicked(evt);
            }
        });
        Tree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TreeKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(Tree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 433, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(517, Short.MAX_VALUE)))
        );

        jMenuBar1.setPreferredSize(new java.awt.Dimension(148, 35));

        File.setText("File");
        File.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        File.setName("File"); // NOI18N

        itemNew.setText("New");
        itemNew.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        itemFolder.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemFolder.setText("Folder");
        itemFolder.setToolTipText("New Folder");
        itemFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemFolderActionPerformed(evt);
            }
        });
        itemNew.add(itemFolder);

        itemFile.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemFile.setText("File");
        itemFile.setToolTipText("New File");
        itemFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemFileActionPerformed(evt);
            }
        });
        itemNew.add(itemFile);

        File.add(itemNew);

        itemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        itemRename.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemRename.setToolTipText("Rename");
        itemRename.setLabel("Rename");
        itemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemRenameActionPerformed(evt);
            }
        });
        File.add(itemRename);

        itemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        itemDelete.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemDelete.setToolTipText("Delete");
        itemDelete.setLabel("Delete");
        itemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDeleteActionPerformed(evt);
            }
        });
        File.add(itemDelete);
        File.add(jSeparator4);

        itemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        itemExit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemExit.setToolTipText("Close ");
        itemExit.setLabel("Exit");
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExitActionPerformed(evt);
            }
        });
        File.add(itemExit);

        jMenuBar1.add(File);

        Edit.setText("Edit");
        Edit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        itemCopy.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemCopy.setText("Copy               Ctrl+C");
        itemCopy.setToolTipText("Copy");
        itemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCopyActionPerformed(evt);
            }
        });
        Edit.add(itemCopy);

        itemCut.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemCut.setText("Cut                  Ctrl+X");
        itemCut.setToolTipText("Cut");
        itemCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCutActionPerformed(evt);
            }
        });
        Edit.add(itemCut);

        itemPaste.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemPaste.setText("Paste               Ctrl+V");
        itemPaste.setToolTipText("Paste");
        itemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPasteActionPerformed(evt);
            }
        });
        Edit.add(itemPaste);
        Edit.add(jSeparator3);

        itemSellectAll.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemSellectAll.setText("Sellect All       Ctrl+A");
        itemSellectAll.setToolTipText("Sellect All");
        itemSellectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSellectAllActionPerformed(evt);
            }
        });
        Edit.add(itemSellectAll);

        jMenuBar1.add(Edit);

        Help.setText("Help");
        Help.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        itemAbout.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemAbout.setText("About us");
        itemAbout.setToolTipText("More information");
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        Help.add(itemAbout);

        jMenuBar1.add(Help);

        View.setText("View");
        View.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        hiddenCheck.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        hiddenCheck.setSelected(true);
        hiddenCheck.setText("Show Hidden");
        hiddenCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiddenCheckActionPerformed(evt);
            }
        });
        View.add(hiddenCheck);

        jMenuBar1.add(View);

        jMenu2.setText("CheckHash");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItem1.setText("CRC32");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem11.setText("MD5");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuItem12.setText("SHA-1");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);

        jMenuItem13.setText("SHA-256");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        loadTree();
        loadTable();
        TableCellListener tcl=new TableCellListener(Table,Tree);
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                java.awt.event.ActionEvent newEvt=new java.awt.event.ActionEvent(this,0,"");
                btnSearchActionPerformed(newEvt);
            }
        };
        jTextField1.addActionListener( action );
    }//GEN-LAST:event_formWindowOpened


    private void TreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TreeMouseClicked
        if(Tree.getLastSelectedPathComponent()==null) return;
        if(saveSelectedNode!=null && openingInTable==false && !isUp && !isCreatingNode && !isBacking && !isForwarding && !isHidden)
        {
            if(saveSelectedNode==(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent())
                return;
        }
        //<editor-fold defaultstate="collapsed" desc="lấy node được chọn">


        
        DefaultMutableTreeNode selectedNode=null;

        if(isForwarding)
        {
            if(strForward.equals("ThisPC")) 
                selectedNode=treeRoot;
            else
            {
                selectedNode=saveSelectedNode;
                for(int i=0;i<selectedNode.getChildCount();i++)
                {
                    DefaultMutableTreeNode tmp=(DefaultMutableTreeNode)selectedNode.getChildAt(i);
                    String path=(String)tmp.getUserObject();
                    if(path.equals(strForward)) 
                    {
                        selectedNode=tmp;
                        break;
                    }     
                }
            }
            
            selectedNode.removeAllChildren();
        }
        else
        if(isBacking)
        {
            if(strBack.equals("ThisPC")) 
                selectedNode=treeRoot;
            else
            {
                selectedNode=saveSelectedNode;
                for(int i=0;i<selectedNode.getChildCount();i++)
                {
                    DefaultMutableTreeNode tmp=(DefaultMutableTreeNode)selectedNode.getChildAt(i);
                    String path=(String)tmp.getUserObject();
                    if(path.equals(strBack)) 
                    {
                        selectedNode=tmp;
                        break;
                    }     
                }
            }
            
            selectedNode.removeAllChildren();
        }
        else
        if(openingInTable)
        { 
            //System.out.println("test"+tableIndex);
            selectedNode=(DefaultMutableTreeNode)((DefaultMutableTreeNode)Tree.getLastSelectedPathComponent()).getChildAt(tableIndex);
            Tree.expandPath(Tree.getLeadSelectionPath());
            openingInTable=false;
        }
        else 
            if(isUp)
            {
                selectedNode=(DefaultMutableTreeNode)saveSelectedNode.getParent();
                selectedNode.removeAllChildren();
                isUp=false;
            }
            else
                if(isCreatingNode)
                {
                    if(textAddress.getText().equals("ThisPC"))
                    {
                        selectedNode=treeRoot;
                    }
                    else
                    {
                        for(int i=0;i<saveSelectedNode.getChildCount();i++)
                            if(((DefaultMutableTreeNode)saveSelectedNode.getChildAt(i)).getUserObject().equals(strCreateNode))
                            {
                                selectedNode=(DefaultMutableTreeNode)saveSelectedNode.getChildAt(i);
                            }
                    }
                    selectedNode.removeAllChildren();
                }
                else
                    if(isHidden)
                    {
                        ((DefaultMutableTreeNode)Tree.getLastSelectedPathComponent()).removeAllChildren();
                        selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
                    }
                    else
                    {
                        ((DefaultMutableTreeNode)Tree.getLastSelectedPathComponent()).removeAllChildren();
                        selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
                    }
            
        //</editor-fold>
        
        //duong dan file dang duoc chon
        String pathStr=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(pathStr);
        java.io.File[] pathss=selectedFile.listFiles();
        if(pathStr=="ThisPC") 
            pathss=java.io.File.listRoots();
    
        for(File path:pathss)
            if(path.isDirectory())
            {
                if(!(path.isHidden() && hiddenCheck.getState()==false))
                    selectedNode.add(new DefaultMutableTreeNode(path.getAbsolutePath()));
            }
        saveSelectedNode=selectedNode;
        ShowInTable(pathss,selectedNode);
        
        DefaultTreeModel model=(DefaultTreeModel)Tree.getModel();
        model.reload(selectedNode);
        
        Tree.setSelectionPath(new TreePath(selectedNode.getPath()));
        Tree.expandRow(selectedNode.getIndex(selectedNode));
        //Tree.expandPath(new TreePath(selectedNode.getPath()));
        textAddress.setText((String)selectedNode.getUserObject());
        
        if(index==-1 || (!saveNode.get(index).equals((String)saveSelectedNode.getUserObject())))
        {
            if(!isCreatingNode && !isBacking && !isForwarding)
            {
                int count=saveNode.size();
                for(int i=index+1;i<=count-1;i++)
                    saveNode.remove(i);
                saveNode.add((String)saveSelectedNode.getUserObject());
                index++;
            }
            
        }
        
        if(saveSelectedNode.toString().equals("ThisPC")) itemRename.setEnabled(false);
        else itemRename.setEnabled(true);
    }//GEN-LAST:event_TreeMouseClicked

    private void pasteAction(){
         if(copy)
         {  
            for(int k =0; k<ArrCoppyFile.length;k++){
            fileCoppyPath = ArrCoppyFile[k];
            System.out.println("cOPPY"+fileCoppyPath.toString());
            String tmpName = new String();
            int fileExist = 1;
            String pasteS = saveSelectedNode.toString();
            tmpS = pasteS.split("\\\\");
            String tmp = new String();
            String tmpE = new String();
            for (int i=0;i<tmpS.length;i++)
            {
                tmp += tmpS[i] + "\\\\" ;
            }
            tmp+=tmpF[k];
            tmpE = tmp;
            System.out.println(tmp+"tmpS");
            filePatsePath = new File(tmp);
            File fEx = new File(tmpE);
            while(fEx.exists()&&fEx.isFile())
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")." +tmp.split("\\.")[1];
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                fileExist +=1;
            }
             while(fEx.exists()&&fEx.isDirectory())
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")";
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                fileExist +=1;
            }
             if(fileCoppyPath.isFile()){
                 try{
                     FileUtils.copyFile(fileCoppyPath, fEx); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
             }
             else if(fileCoppyPath.isDirectory()){
                 try{
                     FileUtils.copyDirectory(fileCoppyPath, fEx); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
             }
            
         }
         }
         if(cut)
         {
            for(int k =0; k<ArrCoppyFile.length;k++){
            fileCoppyPath = ArrCoppyFile[k];
            String tmpName = new String();
            int fileExist = 1;
            String stringClipboard = "..\\clipboard\\"+tmpF[k];
            File Clipboard = new File(stringClipboard);
            String pasteS = saveSelectedNode.toString();
            tmpS = pasteS.split("\\\\");
            String tmp = new String();
            String tmpE = new String();
            for (int i=0;i<tmpS.length;i++)
            {
                tmp += tmpS[i] + "\\\\" ;
            }
             tmp+=tmpF[k];
            tmpE = tmp;
            System.out.println(tmp);
            filePatsePath = new File(tmp);
            File fEx = new File(tmpE);
            while(fEx.exists()&&fEx.isFile())
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")." +tmp.split("\\.")[1];
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                fileExist +=1;
            }
             while(fEx.exists()&&fEx.isDirectory())
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")";
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                fileExist +=1;
            }
            System.out.println(Clipboard.toString());
            if(Clipboard.isFile()){
                 try{
                     FileUtils.copyFile(Clipboard, fEx); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                    Clipboard.delete();
            }
            else if(Clipboard.isDirectory()){
                 try{
                     FileUtils.copyDirectory(Clipboard, fEx); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                 try{
                     FileUtils.deleteDirectory(Clipboard);
                 }
                 catch (IOException e)
                 {
                   System.out.println("Nope");   
                 }
            } 
            }
             cut = false;
         }
    checkChoose();
    }
    
    private void btnPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteActionPerformed
        // TODO add your handling code here:
        pasteAction();
    }//GEN-LAST:event_btnPasteActionPerformed

    private void loadTableWhenAction(){
        String str=(String)saveSelectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        java.io.File[] paths=selectedFile.listFiles();
        
        try{
            saveSelectedNode.removeAllChildren();
            File []fs=selectedFile.listFiles();
            for(int i=0;i<fs.length;i++)
                if(fs[i].isDirectory())
                {
                    if(!(fs[i].isHidden() && hiddenCheck.getState()==false))
                        saveSelectedNode.add(new DefaultMutableTreeNode(fs[i].getPath()));
                }
            DefaultTreeModel model = (DefaultTreeModel)Tree.getModel();

            model.reload(saveSelectedNode);
        }
        catch(Exception ex){
            
        }
        
        ShowInTable(paths,saveSelectedNode);
    }
    
    private void CoppyAction(){
         if(cut)
         {
            for(int k =0; k<ArrCoppyFile.length;k++){
            String stringClipboard = "..\\clipboard\\"+tmpF[k];
            File Clipboard = new File(stringClipboard);
            fileCoppyPath = ArrCoppyFile[k];
            System.out.println(Clipboard.toString());
            System.out.println(fileCoppyPath.toString());
            
            if(Clipboard.isFile()){
                 try{
                     FileUtils.copyFile(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                    Clipboard.delete();
            }
            else if(Clipboard.isDirectory()){
                 try{
                     FileUtils.copyDirectory(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                 try{
                     FileUtils.deleteDirectory(Clipboard);
                 }
                 catch (IOException e)
                 {
                   System.out.println("Nope");   
                 }
            } 
            }
             cut = false;
         }
         //lấy node được chọn
        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
        //if(selectedNode!=null) 
        //selectedNode.removeAllChildren();
      
        String str=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        java.io.File[] pathfirst = selectedFile.listFiles();
        int length=pathfirst.length;
        int dem=0;
        
        paths = new File[length];
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory())
            {
                 paths[dem]=pathfirst[i];
                 dem++;
            }   
        }
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory()==false)
            {
                paths[dem]=pathfirst[i];
                dem++;
            }       
        }
        
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        int[] SelectedRowIndex = Table.getSelectedRows();
        ArrCoppyFile = new File[SelectedRowIndex.length];
        tmpF = new String[SelectedRowIndex.length];
        for (int i = 0; i < SelectedRowIndex.length; i++)
        {
            fileCoppyPath = paths[SelectedRowIndex[i]];
            System.out.println(fileCoppyPath.toString());
            tmpS = fileCoppyPath.toString().split("\\\\");
            String tmp = new String();
            for (int j=0;j<tmpS.length-1;j++)
            {
                tmp += tmpS[j] + "\\\\" ;
            }
            tmpF[i]=tmpS[tmpS.length-1];
            tmp+=tmpF[i];
            System.out.println("new file in coppy!!  "+tmp);
            fileCoppyPath = new File(tmp);
            ArrCoppyFile[i] = fileCoppyPath ;
        }
        copy = true;
        cut = false;
        checkChoose();
    }
    
    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        CoppyAction(); 
    }//GEN-LAST:event_btnCopyActionPerformed

    private void cutAction(){
        if(cut)
         {
            for(int k =0; k<ArrCoppyFile.length;k++){
            String stringClipboard = "..\\clipboard\\"+tmpF[k];
            File Clipboard = new File(stringClipboard);
            fileCoppyPath = ArrCoppyFile[k];
            System.out.println(Clipboard.toString());
            System.out.println(fileCoppyPath.toString());
            if(Clipboard.isFile()){
                 try{
                     FileUtils.copyFile(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                    Clipboard.delete();
            }
            else if(Clipboard.isDirectory()){
                 try{
                     FileUtils.copyDirectory(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                 try{
                     FileUtils.deleteDirectory(Clipboard);
                 }
                 catch (IOException e)
                 {
                   System.out.println("Nope");   
                 }
            } 
            }
             cut = false;
         }
        
         //lấy node được chọn
        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
        //if(selectedNode!=null) 
        selectedNode.removeAllChildren();
      
        String str=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        File[] pathfirst=selectedFile.listFiles();
        int dem=0;
        int length=pathfirst.length;
        java.io.File[] paths = new File[length];
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory())
            {
                 paths[dem]=pathfirst[i];
                 dem++;
            }
               
            
        }
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory()==false)
            {
                paths[dem]=pathfirst[i];
                dem++;
            }
                
        }
        
        
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        
        
        //New line 
        int[] SelectedRowIndex = Table.getSelectedRows();
        ArrCoppyFile = new File[SelectedRowIndex.length];
        tmpF = new String[SelectedRowIndex.length];
        for (int i = 0; i < SelectedRowIndex.length; i++)
        {
        
        //file
        fileCoppyPath = paths[SelectedRowIndex[i]];
        System.out.println(fileCoppyPath.toString());
         
        
        tmpS = fileCoppyPath.toString().split("\\\\");
        String tmp = new String();
        for (int j=0;j<tmpS.length-1;j++)
        {
            tmp += tmpS[j] + "\\\\" ;
        }
        tmpF[i]=tmpS[tmpS.length-1];
        tmp+=tmpF[i];
        System.out.println(tmp);
        fileCoppyPath = new File(tmp);
        ArrCoppyFile[i] = fileCoppyPath ;
        String stringClipboard = "..\\clipboard\\"+tmpF[i];
            File Clipboard = new File(stringClipboard);
        if(fileCoppyPath.isFile())
        {

            try{
                FileUtils.copyFile(fileCoppyPath, Clipboard);
            }
            catch (IOException e)
            {
                //Nope
            }
            fileCoppyPath.delete();
            loadTableWhenAction();
        }
        else if(fileCoppyPath.isDirectory())
        {

            try{
                FileUtils.copyDirectory(fileCoppyPath, Clipboard);
            }
            catch (IOException e)
            {
                //Nope
            }
             try{
                 FileUtils.deleteDirectory(fileCoppyPath);
                 loadTableWhenAction();
            }
            catch (IOException e)
            {
                //Nope
            }
        }
        }
        copy = false;
        cut = true;
        checkChoose();
    }
    
    
    private void btnCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCutActionPerformed
        // TODO add your handling code here:
        cutAction();
    }//GEN-LAST:event_btnCutActionPerformed


    private void TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseClicked
        // TODO add your handling code here:
        jScrollPane2.requestFocus();
        JTable source = (JTable)evt.getSource();
        if ((evt.getClickCount() == 2 || isOpen) && source.getSelectedRow() != -1)
        {
            
            isOpen=false;
            int row = source.rowAtPoint( evt.getPoint() );
            int column = source.columnAtPoint( evt.getPoint() );
            
            String str;//=saveSelectedNode.toString()+"\\"+(String)source.getModel().getValueAt(row, 0);
            str=(String)source.getModel().getValueAt(row, 0);
            if(isSearching)
            {
                System.out.println((String)source.getModel().getValueAt(row, 4));
                str=(String)source.getModel().getValueAt(row, 4);
            }
            File s=new File(str);
            Desktop desktop = Desktop.getDesktop();
            try{
                if(s.exists() && s.isFile()) 
                {
                    desktop.open(s);
                }
                else
                {
                    if(isSearching)
                    {
                        textAddress.setText(str);
                        btnGotoMouseClicked(evt);
                    }
                    else
                    {
                        DefaultTableModel tableModel=(DefaultTableModel) Table.getModel();
                        paths=s.listFiles();
                        openingInTable=true;
                        tableIndex=row;
                        TreeMouseClicked(evt);
                    }
                    
                }
            }
                    
            catch(Exception ex)
            {
            }
            
            if(isSearching)
            {
                //duong dan file dang duoc chon
                String pathStr=(String)saveSelectedNode.getUserObject();
                java.io.File selectedFile =new File(pathStr);
                java.io.File[] pathss=selectedFile.listFiles();
                
                ShowInTable(pathss,saveSelectedNode);
                isSearching=false;
            }
        }
        
    }//GEN-LAST:event_TableMouseClicked

  //<editor-fold defaultstate="collapsed" desc="COPY, CUT, PASTE, BACK, rename, exit,refresh,..">
    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        // TODO add your handling code here:
        AboutForm aboutForm=new AboutForm();
        aboutForm.setLocation(this.getLocation());
        aboutForm.setVisible(true);
    }//GEN-LAST:event_itemAboutActionPerformed

    private void btnUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpMouseClicked
        // TODO add your handling code here:
        if(saveSelectedNode.toString()=="ThisPC") return;
        isUp=true;
        TreeMouseClicked(evt);
    }//GEN-LAST:event_btnUpMouseClicked

    private void btnGotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGotoMouseClicked
        // TODO add your handling code here:
        //Node được chọn là ThisPC
        if(textAddress.getText().equals("ThisPC"))
        {
            isGotoAddress=true;
            TreeMouseClicked(evt);
            return;
        }
        
        //Tách ThisPC ra khỏi chuỗi:
        String[] temp=textAddress.getText().split("\\\\");
        if(temp[0].equals("ThisPC"))
        {
            String tmp=textAddress.getText().substring(7, textAddress.getText().length());
            textAddress.setText(tmp);
            temp=textAddress.getText().split("\\\\");
        }
        
        File f = new File(textAddress.getText());
        if (f.exists() && f.isDirectory()) {
            //Tạo node theo address cho tree
            isCreatingNode=true;
            strCreateNode="";
            saveSelectedNode=treeRoot;
            for(int i=0;i<temp.length;i++)
            {
                if(i==0) strCreateNode+=temp[i]+"\\";
                else 
                    if(i==1) strCreateNode+=temp[i];
                    else
                        strCreateNode+="\\"+temp[i];
                
                TreeMouseClicked(evt);
            }
            if(!saveNode.get(index).equals((String)saveSelectedNode.getUserObject()))
            {
                for(int i=index+1;i<saveNode.size()-1;i++)
                    saveNode.remove(i);
                saveNode.add((String)saveSelectedNode.getUserObject());
                index++;
            }
            
            isCreatingNode=false;
            
        }
        else
        {
            JOptionPane.showMessageDialog(this,
                                  "Please type the address again!",
                                  "Wrong address",
                                  JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnGotoMouseClicked

    private void btnBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackMouseClicked
        // TODO add your handling code here:
        try
        {
            if(index==-1) 
            return;
            if(index==0)
            {
                index--;
                isBacking=true;
                strBack="ThisPC";
                TreeMouseClicked(evt);
                isBacking=false;
                return;
            }
            isBacking=true;
            index--;
            String[] temp=saveNode.get(index).split("\\\\");
            strBack="";
            saveSelectedNode=treeRoot;
            for(int i=0;i<temp.length;i++)
            {
                if(i==0) strBack+=temp[i]+"\\";
                else 
                    if(i==1) strBack+=temp[i];
                    else
                        strBack+="\\"+temp[i];
                TreeMouseClicked(evt);
            }        



            isBacking=false;
        }
        catch (Exception e)
        {
            
        }
        
        
    }//GEN-LAST:event_btnBackMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent(); 
        selectedNode.removeAllChildren();
        saveSelectedNode=selectedNode;
        String str=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        File[] pathfirst=selectedFile.listFiles();
        int dem=0;
        int length=pathfirst.length;
        paths = new File[length];
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory())
            {
                 paths[dem]=pathfirst[i];
                 dem++;
            }   
        }
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory()==false)
            {
                paths[dem]=pathfirst[i];
                dem++;
            }       
        }
        
        
       DefaultTableModel model = (DefaultTableModel) Table.getModel();
        
        
        //New line 
        int[] SelectedRowIndex = Table.getSelectedRows();
        ArrCoppyFile = new File[SelectedRowIndex.length];
        tmpF = new String[SelectedRowIndex.length];
        if(SelectedRowIndex.length!=0)
        {
            int ck=JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa file này?", "Cảnh báo!", JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);
            if (ck==1) return;
        }
        for (int i = 0; i < SelectedRowIndex.length; i++)
        {
        
        //file
        fileCoppyPath = paths[SelectedRowIndex[i]];
        System.out.println(fileCoppyPath.toString());
         
        
        tmpS = fileCoppyPath.toString().split("\\\\");
        String tmp = new String();
        for (int j=0;j<tmpS.length-1;j++)
        {
            tmp += tmpS[j] + "\\\\" ;
        }
        tmpF[i]=tmpS[tmpS.length-1];
        tmp+=tmpF[i];
        System.out.println(tmp);
        fileCoppyPath = new File(tmp);
        ArrCoppyFile[i] = fileCoppyPath ;
        if(fileCoppyPath.isFile())
        {

          
            fileCoppyPath.delete();
            loadTableWhenAction();
        }
        else if(fileCoppyPath.isDirectory())
        {

             try{
                 FileUtils.deleteDirectory(fileCoppyPath);
                 loadTableWhenAction();
            }
            catch (IOException e)
            {
                //Nope
            }
        }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
       if(cut)
         {
            for(int k =0; k<ArrCoppyFile.length;k++){
            fileCoppyPath = ArrCoppyFile[k];
            String stringClipboard = "..\\clipboard\\"+tmpF[k];
            File Clipboard = new File(stringClipboard);
            System.out.println(Clipboard.toString());
            System.out.println(fileCoppyPath.toString());
            if(Clipboard.isFile()){
                 try{
                     FileUtils.copyFile(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                    Clipboard.delete();
            }
            else if(Clipboard.isDirectory()){
                 try{
                     FileUtils.copyDirectory(Clipboard, fileCoppyPath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                 try{
                     FileUtils.deleteDirectory(Clipboard);
                 }
                 catch (IOException e)
                 {
                   System.out.println("Nope");   
                 }
            } 
            }
             cut = false;
         }
    }//GEN-LAST:event_formWindowClosing

    private void btnForwardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnForwardMouseClicked
        // TODO add your handling code here:
        if(index+1==saveNode.size()) return;
        index++;
        isForwarding=true;
        String[] temp=saveNode.get(index).split("\\\\");
        strForward="";
        saveSelectedNode=treeRoot;
        for(int i=0;i<temp.length;i++)
        {
            if(i==0) strForward+=temp[i]+"\\";
            else 
                if(i==1) strForward+=temp[i];
                else
                    strForward+="\\"+temp[i];
            TreeMouseClicked(evt);
        }     
        
        isForwarding=false;
    }//GEN-LAST:event_btnForwardMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_formWindowClosed

    private void itemSellectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSellectAllActionPerformed
        // TODO add your handling code here:
        if(!isSelectAll)
        {
            isSelectAll=true;
            Table.selectAll();
        }
        else
        {
            Table.clearSelection();
            isSelectAll=false;
        }
        
    }//GEN-LAST:event_itemSellectAllActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
      
    
    }//GEN-LAST:event_formKeyPressed

    private void TableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TableKeyPressed

    }//GEN-LAST:event_TableKeyPressed

    private void TreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TreeKeyPressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_TreeKeyPressed

    private void checkChoose()
    {
        if(copy)
        {
            btnCopy.setForeground(Color.BLUE);
        }
        if(cut)
        {
            btnCut.setForeground(Color.BLUE);
        }
        if(!copy) {
             btnCopy.setForeground(Color.BLACK);
        }
         if(!cut) {
             btnCut.setForeground(Color.BLACK);
        }
    }

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
        // TODO add your handling code here:
        Table.clearSelection();

    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void jScrollPane2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane2KeyPressed
        // TODO add your handling code here:
        if ((evt.getKeyCode() == KeyEvent.VK_A)&&((evt.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK)!=0)) {
                  if(!isSelectAll)
                    {
                        isSelectAll=true;
                        Table.selectAll();
                    }
                    else
                    {
                        Table.clearSelection();
                        isSelectAll=false;
                    }
        }
        if ((evt.getKeyCode() == KeyEvent.VK_C)&&((evt.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK)!=0)) {
                  CoppyAction();
        }
        if ((evt.getKeyCode() == KeyEvent.VK_X)&&((evt.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK)!=0)) {
                  cutAction();
        }
         if ((evt.getKeyCode() == KeyEvent.VK_V)&&((evt.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK)!=0)) {
                  pasteAction();
        }
        
    }//GEN-LAST:event_jScrollPane2KeyPressed

    private void itemFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemFolderActionPerformed
        // TODO add your handling code here:
        if(saveSelectedNode.toString().equals("ThisPC"))return;
        int dem=1;
        String str="New folder";
        
        DefaultTableModel model=(DefaultTableModel) Table.getModel();
        
        String path=saveSelectedNode.toString();
        
        while(new File(path+"\\"+str+"("+dem+")").exists()){
            dem++;
        };
        
        path=path+"\\"+str+"("+dem+")";
        File newFolder=new File(path);
        
        try{
            newFolder.mkdir();
        }
        catch(Exception ex){
            
        }
        
        loadTableWhenAction();
        
        
        String abc=newFolder.getName();
        for(int i=0;i<Table.getRowCount();i++)
        {
            String temp=(String)Table.getValueAt(i, 0);
            if(temp.equals(abc))
            {
                Table.setRowSelectionInterval(i, i);
                isTaoMoiThuMuc=true;
                Table.editCellAt(i, 0);
                return;
            }   
        }    
    }//GEN-LAST:event_itemFolderActionPerformed

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_itemExitActionPerformed

    private void itemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemRenameActionPerformed
        // TODO add your handling code here:
        
        isRenameClick=true; 
        int rowSua=Table.getSelectedRow();
        Table.editCellAt(rowSua, 0);
    }//GEN-LAST:event_itemRenameActionPerformed

    private void itemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDeleteActionPerformed
        // TODO add your handling code here:
        btnDeleteActionPerformed(evt);
    }//GEN-LAST:event_itemDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadTableWhenAction();
        //duong dan file dang duoc chon
        
    }//GEN-LAST:event_btnRefreshActionPerformed


    private void itemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCopyActionPerformed
        // TODO add your handling code here:
        CoppyAction();
    }//GEN-LAST:event_itemCopyActionPerformed

    private void itemCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCutActionPerformed
        // TODO add your handling code here:
        cutAction();
    }//GEN-LAST:event_itemCutActionPerformed

    private void itemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPasteActionPerformed
        // TODO add your handling code here:
        pasteAction();
    }//GEN-LAST:event_itemPasteActionPerformed

    private void itemFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemFileActionPerformed
        // TODO add your handling code here:
        
        if(saveSelectedNode.toString().equals("ThisPC"))return;
        int dem=1;
        String str="New File";
        DefaultTableModel model=(DefaultTableModel) Table.getModel();
        
        String path=saveSelectedNode.toString();
        
        while(new File(path+"\\"+str+"("+dem+")").exists()){
            dem++;
        };
        
        path=path+"\\"+str+"("+dem+")";
        File xx=new File(path);
        
        try{
            xx.createNewFile();
        }
        catch(Exception ex){
            
        }
        loadTableWhenAction();
        
        
        String abc=xx.getName();
        for(int i=0;i<Table.getRowCount();i++)
        {
            String temp=(String)Table.getValueAt(i, 0);
            if(temp.equals(abc))
            {
                Table.setRowSelectionInterval(i, i);
                isTaoMoiFile=true;
                Table.editCellAt(i, 0);
                return;
            }   
        }
        
        
        
    }//GEN-LAST:event_itemFileActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackActionPerformed

    //</editor-fold>
    
    private void TableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseReleased
        if(saveSelectedNode==null || saveSelectedNode.toString().equals("ThisPC")) 
            return;
        if(evt.isPopupTrigger())
        {
            setEnableItems();
            if(Table.getSelectedRowCount()>0)
            {
                setHiddenCheck.setEnabled(true);
                if(Table.getSelectedRowCount()>1) setHiddenCheck.setEnabled(false);
                else
                {
                    JTable source = (JTable)evt.getSource();
                    int row = source.rowAtPoint( evt.getPoint() );
                    int column = source.columnAtPoint( evt.getPoint() );
            
                    String str=saveSelectedNode.toString()+"\\"+(String)source.getModel().getValueAt(row, 0);
                    File s=new File(str);
                    if(s.isHidden()) setHiddenCheck.setState(true);
                    else setHiddenCheck.setState(false);
                }
                popupMenu.show(this, 0, 0);
                popupMenu.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            else
            {
                popupMenuPanel.show(this, 0, 0);
                popupMenuPanel.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
            
        }
    }//GEN-LAST:event_TableMouseReleased

    private void jScrollPane2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseReleased
        // TODO add your handling code here:
        if(saveSelectedNode==null || saveSelectedNode.toString().equals("ThisPC")) 
            return;
        jScrollPane2.requestFocus();
        System.out.println("I'm in croll pane 2");
        if(evt.isPopupTrigger())
        {
            setEnableItems();
            Table.clearSelection();
            popupMenuPanel.show(this, 0, 0);
            popupMenuPanel.setLocation(MouseInfo.getPointerInfo().getLocation());
        }
    }//GEN-LAST:event_jScrollPane2MouseReleased
//<editor-fold defaultstate="collapsed" desc="JMenuItems, HIDDEN">
    private void SetUpPopupMenus()
    {
        
        jMenuItem2.setText("Copy             Ctrl+C");
        jMenuItem3.setText("Cut                Ctrl+X");
        jMenuItem4.setText("Delete");
        jMenuItem5.setText("Rename         F2");
        jMenuItem6.setText("Select All        Ctrl+A");
        
        jMenuItem7.setText("Refresh");
        jMenuItem8.setText("Paste");
        jMenuItem9.setText("Folder");
        jMenuItem10.setText("File");
    }
    
    private void setEnableItems()
    {
        if(Table.getSelectedRowCount()>0) jMenuItem4.setEnabled(true);
        else jMenuItem4.setEnabled(false);
        if(Table.getSelectedRowCount()>1) jMenuItem5.setEnabled(false);
        else jMenuItem5.setEnabled(true);
        
        if(copy || cut) jMenuItem8.setEnabled(true);
        else jMenuItem8.setEnabled(false);
        
        
    }
    
    

    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        btnCopyActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        btnCutActionPerformed(evt);
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        btnDeleteActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        itemRenameActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        itemSellectAllActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        btnRefreshActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        btnPasteActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        itemFolderActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        itemFileActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void hiddenCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiddenCheckActionPerformed
        // TODO add your handling code here:
        java.awt.event.MouseEvent evtx=new java.awt.event.MouseEvent(this,1,1,1,1,1,1,true);
        isHidden=true;
        TreeMouseClicked(evtx);
        
    }//GEN-LAST:event_hiddenCheckActionPerformed

    private void setHiddenCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setHiddenCheckActionPerformed
        // TODO add your handling code here:
        //JTable source = (JTable)evt.getSource();
        
        int row = Table.getSelectedRow();
        String str=saveSelectedNode.toString()+"\\"+(String)Table.getModel().getValueAt(row, 0);
        File s=new File(str);
        Path filePath = Paths.get(str);
        setHiddenAttrib(filePath,!(s.isHidden()));
        //show lại trên table
        String pathStr=(String)saveSelectedNode.getUserObject();
        java.io.File selectedFile =new File(pathStr);
        java.io.File[] pathss=selectedFile.listFiles();
        if(pathStr=="ThisPC") 
            pathss=java.io.File.listRoots();
    
        ShowInTable(pathss,saveSelectedNode);
    }//GEN-LAST:event_setHiddenCheckActionPerformed
//</editor-fold>
    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        if(saveSelectedNode.toString().equals("ThisPC")) return;
        isSearching=true;
        DefaultTableModel tableModel=(DefaultTableModel)Table.getModel();
        
        while(tableModel.getRowCount() > 0)
        {
            tableModel.removeRow(0);
        }
        String pathStr=(String)saveSelectedNode.getUserObject();
        java.io.File selectedFile =new File(pathStr);
        
        addItemSearchToTable(selectedFile,tableModel);
        
        //Table.getColumnModel().getColumn(0).setCellRenderer(new TableRender());
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGotoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGotoActionPerformed

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
        // TODO add your handling code here:
        if(jTextField1.getText().equals("Search.."))
            jTextField1.setText("");
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jTextField1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseExited
        // TODO add your handling code here:
        if(jTextField1.getText().equals(""))
            jTextField1.setText("Search..");
    }//GEN-LAST:event_jTextField1MouseExited
    private static String toHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        int r=Table.getSelectedRow();
        if(r<0) return;
        String path=Table.getValueAt(r, 0).toString();
        if(new File(path).isFile()==false)
            return;
        File f=new File(path);
        String name=f.getName();
        long size=f.length()/1000;
        String hash=(toHex(Hash.MD5.checksum(new File(path)))+"").toUpperCase();
        CheckFormInformation c=new CheckFormInformation("MD5", hash, name, size);
        c.setVisible(true);
        //JOptionPane.showMessageDialog(null,(toHex(Hash.MD5.checksum(new File(path)))+"").toUpperCase(),"MD5",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        int r=Table.getSelectedRow();
        if(r<0) return;
        String path=Table.getValueAt(r, 0).toString();
        if(new File(path).isFile()==false)
            return;
        try{
            long crc=CRC32.checksumMappedFile(path);
            File f=new File(path);
            String name=f.getName();
            long size=f.length()/1000;
            String hash=(Long.toHexString(crc)+"").toUpperCase();
            CheckFormInformation c=new CheckFormInformation("CRC32", hash, name, size);
            c.setVisible(true);
            //JOptionPane.showMessageDialog(null,(Long.toHexString(crc)+"").toUpperCase(),"CRC32",JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e){
            
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        int r=Table.getSelectedRow();
        if(r<0) return;
        String path=Table.getValueAt(r, 0).toString();
        if(new File(path).isFile()==false)
            return;
        File f=new File(path);
        String name=f.getName();
        long size=f.length()/1000;
        String hash=(toHex(Hash.SHA1.checksum(new File(path)))+"").toUpperCase();
        CheckFormInformation c=new CheckFormInformation("SHA1", hash, name, size);
        c.setVisible(true);
        //JOptionPane.showMessageDialog(null,(toHex(Hash.SHA1.checksum(new File(path)))+"").toUpperCase(),"SHA-1",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        int r=Table.getSelectedRow();
        if(r<0) return;
        String path=Table.getValueAt(r, 0).toString();
        if(new File(path).isFile()==false)
            return;
        File f=new File(path);
        String name=f.getName();
        long size=f.length()/1000;
        String hash=(toHex(Hash.SHA256.checksum(new File(path)))+"").toUpperCase();
        CheckFormInformation c=new CheckFormInformation("SHA256", hash, name, size);
        c.setVisible(true);
        //JOptionPane.showMessageDialog(null,(toHex(Hash.SHA256.checksum(new File(path)))+"").toUpperCase(),"SHA-256",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void btnExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtractActionPerformed
        // TODO add your handling code here:
        Extract();
    }//GEN-LAST:event_btnExtractActionPerformed

        
    private void addItemSearchToTable(File file,DefaultTableModel tableModel){
        java.io.File[] pathss=file.listFiles();
        int n=pathss.length;
        Object row[]=new Object[5];
        for(int i=0;i<n;i++)
            if(!(pathss[i].isHidden() && hiddenCheck.getState()==false))
                if(pathss[i].isDirectory() )
                {
                    
                    if(pathss[i].getName().toLowerCase().indexOf(jTextField1.getText().toLowerCase())!=-1)
                    {
//                        if(pathss[i].getAbsolutePath().length()!=3)
//                        row[0]=pathss[i].getName();              
//                        else
                            row[0]=pathss[i].getAbsolutePath();
                        Date d = new Date(pathss[i].lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                        String strDate = formatter.format(d);
                        row[1]=strDate;
                        row[2]="Folder";
                        row[3]="N/A";
                        row[4]=pathss[i].getAbsolutePath();
                        tableModel.addRow(row);
                    }
                    addItemSearchToTable(pathss[i],tableModel);
                }
        
        for(int i=0;i<n;i++)
            if(!(pathss[i].isHidden() && hiddenCheck.getState()==false))
                if(pathss[i].isFile() )
                {
                    
                    if(pathss[i].getName().toLowerCase().indexOf(jTextField1.getText().toLowerCase())!=-1)
                    {
                        
                        //row[0]=pathss[i].getName();
                        row[0]=pathss[i].getAbsolutePath();
                        Date d = new Date(pathss[i].lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                        String strDate = formatter.format(d);
                        row[1]=strDate;
                        row[2]="File";
                        row[3]=pathss[i].length()/1000+" KB";
                        row[4]=pathss[i].getAbsolutePath();
                        tableModel.addRow(row);
                    }
                    
                }
        
    }
    
    private static void setHiddenAttrib(Path filePath,boolean hide) {        
        try {
            DosFileAttributes attr = Files.readAttributes(filePath, DosFileAttributes.class);
            System.out.println(filePath.getFileName() + " Hidden attribute is " + attr.isHidden());
            Files.setAttribute(filePath, "dos:hidden", hide);
            attr = Files.readAttributes(filePath, DosFileAttributes.class);
            System.out.println(filePath.getFileName() + " Hidden attribute is " + attr.isHidden());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    void ShowInTable(File[] paths,DefaultMutableTreeNode selectedNode)
    {
        int dem=0;
        int n=paths.length;
        Object row[]=new Object[4];

        DefaultTableModel tableModel=(DefaultTableModel)Table.getModel();
        
        while(tableModel.getRowCount() > 0)
        {
            tableModel.removeRow(0);
        }
        

        
        for(int i=0;i<n;i++)
            if(!(paths[i].isHidden() && hiddenCheck.getState()==false))
                if(paths[i].isDirectory() )
                {

//                    if(paths[i].getAbsolutePath().length()!=3)
//                    row[0]=paths[i].getName();              
//                    else
                        row[0]=paths[i].getAbsolutePath();
                    Date d = new Date(paths[i].lastModified());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                    String strDate = formatter.format(d);
                    row[1]=strDate;
                    row[2]="Folder";
                    if(selectedNode.toString().equals("ThisPC")) row[2]="Disk";
                    row[3]="N/A";
                    if(selectedNode.toString().equals("ThisPC")) row[3]="loading...";
                    tableModel.addRow(row);
                }
        for(int i=0;i<n;i++)
            if(!(paths[i].isHidden() && hiddenCheck.getState()==false))
                if(paths[i].isFile() )
                {
                   
                    //row[0]=paths[i].getName();
                    row[0]=paths[i].getAbsolutePath();
                    Date d = new Date(paths[i].lastModified());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                    String strDate = formatter.format(d);
                    row[1]=strDate;
                    row[2]="File";
                    if(selectedNode.toString().equals("ThisPC")) row[2]="Disk";
                    row[3]=paths[i].length()/1000+" KB";
                    tableModel.addRow(row);
                }
        
        
        
        Table.getColumnModel().getColumn(0).setCellRenderer(new TableRender());
        
        
    }
    
    private void loadTree()
    {
        paths = java.io.File.listRoots();

        DefaultTreeModel model=(DefaultTreeModel)Tree.getModel();
        DefaultMutableTreeNode ThisPC=(DefaultMutableTreeNode)model.getRoot();
        
      
        for(File path:paths)
        {
            String pathStr=path.getAbsolutePath();
            ThisPC.add(new DefaultMutableTreeNode(pathStr));
            model.reload();
        }
        saveSelectedNode=ThisPC;
        treeRoot=saveSelectedNode;
    }
    
    private void loadTable()
    {
        Table.setBackground(Color.WHITE);
        Table.setDefaultEditor(Object.class, new CustomEditor(new JTextField()));
        JTableHeader header = Table.getTableHeader();
        header.setPreferredSize(new Dimension(100, 30));
        header.setFont(new Font("",Font.PLAIN,18));
        int n=0;
        Object name[]=new Object[n];
        Object date_modified[]=new Object[n];
        Object type[]=new Object[n];
        Object size[]=new Object[n];
        Object path[]=new Object[n];
        DefaultTableModel tableModel=(DefaultTableModel)Table.getModel();
        tableModel.addColumn("Name",name);
        tableModel.addColumn("Date modified",date_modified);
        tableModel.addColumn("Type",type);
        tableModel.addColumn("Size",size);
        tableModel.addColumn("Path",path);
    }
    
    public void Extract(){
        String tmpEx;
         //lấy node được chọn
        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
        //if(selectedNode!=null) 
        //selectedNode.removeAllChildren();
      
        String str=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        java.io.File[] pathfirst = selectedFile.listFiles();
        int length=pathfirst.length;
        int dem=0;
        
        paths = new File[length];
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory())
            {
                 paths[dem]=pathfirst[i];
                 dem++;
            }   
        }
        for(int i=0;i<length;i++)
        {
            if(pathfirst[i].isDirectory()==false)
            {
                paths[dem]=pathfirst[i];
                dem++;
            }       
        }
        
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        int SelectedRowIndex = Table.getSelectedRow();
        fileEx = paths[SelectedRowIndex];
        System.out.println(fileEx.toString());
        tmpEx=fileEx.getName();
        if(tmpEx.lastIndexOf(".") != -1 && tmpEx.lastIndexOf(".") != 0)
        {
         tmpEx = tmpEx.substring(tmpEx.lastIndexOf(".")+1);
         System.out.println(tmpEx);
        }
        else 
            return;
         
        if(tmpEx.equals("zip"))
        {
            System.out.println("It's zip");
             // Tạo một buffer (Bộ đệm).
        byte[] buffer = new byte[1024];
 
        ZipInputStream zipIs = null;
        try {
            // Tạo đối tượng ZipInputStream để đọc file từ 1 đường dẫn (path).
            zipIs = new ZipInputStream(new FileInputStream(fileEx));
 
            ZipEntry entry = null;
            // Duyệt từng Entry (Từ trên xuống dưới cho tới hết)
            while ((entry = zipIs.getNextEntry()) != null) {
                String entryName = entry.getName();
                String outFileName = selectedFile + "\\\\"+ entryName;
                System.out.println("Unzip: " + outFileName);
 
                if (entry.isDirectory()) {
                    // Tạo các thư mục.
                    new File(outFileName).mkdirs();
                } else {
                    // Tạo một Stream để ghi dữ liệu vào file.
                    FileOutputStream fos = new FileOutputStream(outFileName);
 
                    int len;
                    // Đọc dữ liệu trên Entry hiện tại.
                    while ((len = zipIs.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
 
                    fos.close();
                }
 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipIs.close();
            } catch (Exception e) {
            }
        }
    }
    }
      
 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Edit;
    private javax.swing.JMenu File;
    private javax.swing.JMenu Help;
    private javax.swing.JTable Table;
    private javax.swing.JTree Tree;
    private javax.swing.JMenu View;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnCut;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExtract;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnGoto;
    private javax.swing.JButton btnPaste;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUp;
    private javax.swing.JCheckBoxMenuItem hiddenCheck;
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JMenuItem itemCopy;
    private javax.swing.JMenuItem itemCut;
    private javax.swing.JMenuItem itemDelete;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemFile;
    private javax.swing.JMenuItem itemFolder;
    private javax.swing.JMenu itemNew;
    private javax.swing.JMenuItem itemPaste;
    private javax.swing.JMenuItem itemRename;
    private javax.swing.JMenuItem itemSellectAll;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lbAddress;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JPopupMenu popupMenuPanel;
    private javax.swing.JCheckBoxMenuItem setHiddenCheck;
    private javax.swing.JTextField textAddress;
    // End of variables declaration//GEN-END:variables

}

