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
import java.awt.List;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.DateFormat;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.commons.io.FileUtils;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
/**
 *
 * @author User
 */
public class MainForm extends javax.swing.JFrame {

    //github
    //khoa
    String[] tmpS;
    String tmpF;
    boolean openingInTable=false;
    int tableIndex=-1;
    boolean isUp=false;
    boolean isGotoAddress=false;
    boolean isCreatingNode=false;
    boolean isBacking=false;
    boolean isForwarding=false;
    String strCreateNode=null;
    String strBack;
    String strForward;
    private boolean copy = false;
    private boolean cut = false;
    private File fileCoppyPath;
    private File filePatsePath;
    private ArrayList<String> saveNode = new ArrayList<>();
    int index=-1;
    
    private boolean isRenameClick=false;
    private boolean isTaoMoiThuMuc=false;
    private File[] paths;
    private DefaultMutableTreeNode saveSelectedNode=null;
    private DefaultMutableTreeNode treeRoot=null;
    public MainForm() {
        initComponents();
        //Tree.setCellRenderer(new TreeNodeRender());
    }
    class TreeNodeRender extends DefaultTreeCellRenderer{
        private JLabel label;

        TreeNodeRender() {
            label = new JLabel();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            JLabel label=new JLabel();
            if (o instanceof String) {
                String str = (String) o;
                Icon ic=FileSystemView.getFileSystemView().getSystemIcon(new File(str));
                label.setIcon(ic);                   
                if(str.length()==3)
                        label.setText(str);
                    else
                        label.setText(new File(str).getName());
                label.setIcon(ic);                   
                }
            return label;
        }
    }
    class Render extends DefaultTableCellRenderer{
            public Render() { 
            
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                String path=(String)table.getModel().getValueAt(row, column);
                String selectedFilePath=saveSelectedNode.toString()+"\\"+path;
                this.setOpaque(true);
                this.setIcon(FileSystemView.getFileSystemView().getSystemIcon(new File(selectedFilePath)));
                this.setText(path);
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
            if((column==0) && (isRenameClick==false && isTaoMoiThuMuc==false)) return null;       
            
            if (value != null)
                {
                    editor.setText(value.toString());
                }
            if(value==null)
                {
                    System.out.println("nullllll");
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
			//action.actionPerformed(event);
                        //JOptionPane.showMessageDialog(null, "đã edit");
                        //System.out.println("giá trị cũ "+oldValue+" ,giá trị mới: "+newValue);
                        String fileOld=saveSelectedNode.toString()+"\\"+oldValue;
                        String fileNew=saveSelectedNode.toString()+"\\"+newValue;
                        File fO=new File(fileOld);
                        File fN=new File(fileNew);
                        fO.renameTo(fN);
                        loadTableWhenAction();
		}
                isRenameClick=false;
                isTaoMoiThuMuc=false;
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
        btnView = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tree = new javax.swing.JTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        File = new javax.swing.JMenu();
        itemNew = new javax.swing.JMenu();
        itemFolder = new javax.swing.JMenuItem();
        itemRename = new javax.swing.JMenuItem();
        itemDelete = new javax.swing.JMenuItem();
        itemExit = new javax.swing.JMenuItem();
        Edit = new javax.swing.JMenu();
        itemCopy = new javax.swing.JMenuItem();
        itemCut = new javax.swing.JMenuItem();
        itemPaste = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        itemSellectAll = new javax.swing.JMenuItem();
        View = new javax.swing.JMenu();
        Help = new javax.swing.JMenu();
        itemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
        btnBack.setFocusable(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackMouseClicked(evt);
            }
        });
        jToolBar2.add(btnBack);

        btnForward.setBackground(new java.awt.Color(255, 255, 255));
        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/forward.png"))); // NOI18N
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
        btnGoto.setFocusable(false);
        btnGoto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGotoMouseClicked(evt);
            }
        });
        jToolBar2.add(btnGoto);

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setRollover(true);
        jToolBar1.setEnabled(false);

        btnCopy.setBackground(new java.awt.Color(255, 255, 255));
        btnCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/copy.png"))); // NOI18N
        btnCopy.setText("Copy");
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
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRefresh.setMinimumSize(new java.awt.Dimension(70, 40));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRefresh);

        btnView.setBackground(new java.awt.Color(255, 255, 255));
        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/explorer/image/view.png"))); // NOI18N
        btnView.setText("View");
        btnView.setFocusable(false);
        btnView.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnView.setMinimumSize(new java.awt.Dimension(70, 40));
        jToolBar1.add(btnView);

        jSplitPane1.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPane1.setDividerLocation(250);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 985, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
                .addComponent(jSplitPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 491, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)))
        );

        jMenuBar1.setPreferredSize(new java.awt.Dimension(148, 35));

        File.setText("File");
        File.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        File.setName("File"); // NOI18N

        itemNew.setText("New");
        itemNew.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        itemFolder.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemFolder.setText("Folder");
        itemFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemFolderActionPerformed(evt);
            }
        });
        itemNew.add(itemFolder);

        File.add(itemNew);

        itemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        itemRename.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemRename.setLabel("Rename");
        itemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemRenameActionPerformed(evt);
            }
        });
        File.add(itemRename);

        itemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        itemDelete.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemDelete.setLabel("Delete");
        itemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDeleteActionPerformed(evt);
            }
        });
        File.add(itemDelete);

        itemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        itemExit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
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

        itemCopy.setText("Copy               Ctrl+C");
        Edit.add(itemCopy);

        itemCut.setText("Cut                  Ctrl+X");
        Edit.add(itemCut);

        itemPaste.setText("Paste               Ctrl+V");
        Edit.add(itemPaste);
        Edit.add(jSeparator3);

        itemSellectAll.setText("Sellect All       Ctrl+A");
        itemSellectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSellectAllActionPerformed(evt);
            }
        });
        Edit.add(itemSellectAll);

        jMenuBar1.add(Edit);

        View.setText("View");
        View.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuBar1.add(View);

        Help.setText("Help");
        Help.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        itemAbout.setText("About us");
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        Help.add(itemAbout);

        jMenuBar1.add(Help);

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
    }//GEN-LAST:event_formWindowOpened


    private void TreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TreeMouseClicked
        
        if(saveSelectedNode!=null && openingInTable==false && !isUp && !isCreatingNode && !isBacking && !isForwarding)
        {
            if(saveSelectedNode==(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent())
                return;
        }
        
        //lấy node được chọn
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
                {
                    ((DefaultMutableTreeNode)Tree.getLastSelectedPathComponent()).removeAllChildren();
                    selectedNode=(DefaultMutableTreeNode)Tree.getLastSelectedPathComponent();
                }
            
      
        //duong dan file dang duoc chon
        String pathStr=(String)selectedNode.getUserObject();
        java.io.File selectedFile =new File(pathStr);
        java.io.File[] pathss=selectedFile.listFiles();
        if(pathStr=="ThisPC") 
            pathss=java.io.File.listRoots();
    
        for(File path:pathss)
            if(path.isDirectory())
            {
                selectedNode.add(new DefaultMutableTreeNode(path.getAbsolutePath()));
            }
        ShowInTable(pathss);
        saveSelectedNode=selectedNode;
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
            
    }//GEN-LAST:event_TreeMouseClicked


    
    private void btnPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteActionPerformed
        // TODO add your handling code here:
         if(copy)
         {
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
            tmp+=tmpF;
            tmpE = tmp;
            System.out.println(tmp);
            filePatsePath = new File(tmp);
            File fEx = new File(tmpE);
            while(fEx.exists()&&(fileCoppyPath.toString().equals(filePatsePath.toString())==false))
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")." +tmp.split("\\.")[1];
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                filePatsePath.renameTo(fEx);
                fileExist +=1;
            }
            
             if(fileCoppyPath.isFile()){
                 try{
                     FileUtils.copyFile(fileCoppyPath, filePatsePath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
             }
             else if(fileCoppyPath.isDirectory()){
                 try{
                     FileUtils.copyDirectory(fileCoppyPath, filePatsePath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
             }
         }
         if(cut)
         {
            String tmpName = new String();
            int fileExist = 1;
            String stringClipboard = "..\\clipboard\\"+tmpF;
            File Clipboard = new File(stringClipboard);
            String pasteS = saveSelectedNode.toString();
            tmpS = pasteS.split("\\\\");
            String tmp = new String();
            String tmpE = new String();
            for (int i=0;i<tmpS.length;i++)
            {
                tmp += tmpS[i] + "\\\\" ;
            }
             tmp+=tmpF;
            tmpE = tmp;
            System.out.println(tmp);
            filePatsePath = new File(tmp);
            File fEx = new File(tmpE);
            while(fEx.exists()&&(fileCoppyPath.toString().equals(filePatsePath.toString())==false))
            {
                System.out.println("in while"+fileCoppyPath.toString());
                System.out.println(filePatsePath.toString());
                tmpName = tmp.split("\\.")[0] + "("+fileExist+")." +tmp.split("\\.")[1];
                System.out.println("check"+ tmpName);
                fEx = new File(tmpName);
                filePatsePath.renameTo(fEx);
                fileExist +=1;
            }
            System.out.println(Clipboard.toString());
            if(Clipboard.isFile()){
                 try{
                     FileUtils.copyFile(Clipboard, filePatsePath); 
                     loadTableWhenAction();
                    }  
                 catch (IOException e){
                         System.out.println("Nope");
                    }
                    Clipboard.delete();
            }
            else if(Clipboard.isDirectory()){
                 try{
                     FileUtils.copyDirectory(Clipboard, filePatsePath); 
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
             cut = false;
         }

    }//GEN-LAST:event_btnPasteActionPerformed

    private void loadTableWhenAction()
    {
        String str=(String)saveSelectedNode.getUserObject();
        java.io.File selectedFile =new File(str);
        //System.out.println(str);
        //java.io.File selectedFile =(java.io.File)saveSelectedNode.getUserObject();
        java.io.File[] paths=selectedFile.listFiles();
        
        try{
            saveSelectedNode.removeAllChildren();
            File []fs=selectedFile.listFiles();
            for(int i=0;i<fs.length;i++)
                if(fs[i].isDirectory())
                {
                    saveSelectedNode.add(new DefaultMutableTreeNode(fs[i].getPath()));
                }
            DefaultTreeModel model = (DefaultTreeModel)Tree.getModel();

            model.reload(saveSelectedNode);
        }
        catch(Exception ex){
            
        }
        
        ShowInTable(paths);
    }
    
    
    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        if(cut)
         {
            String stringClipboard = "..\\clipboard\\"+tmpF;
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
        int SelectedRowIndex = Table.getSelectedRow();
        
        fileCoppyPath = paths[SelectedRowIndex];
        System.out.println(fileCoppyPath.toString());
         
        
        tmpS = fileCoppyPath.toString().split("\\\\");
        String tmp = new String();
        for (int i=0;i<tmpS.length-1;i++)
        {
            tmp += tmpS[i] + "\\\\" ;
        }
        tmpF=tmpS[tmpS.length-1];
        tmp+=tmpF;
        System.out.println(tmp);
        fileCoppyPath = new File(tmp);
        copy = true;
        cut = false;
    }//GEN-LAST:event_btnCopyActionPerformed

    private void btnCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCutActionPerformed
        // TODO add your handling code here:
        if(cut)
         {
            String stringClipboard = "..\\clipboard\\"+tmpF;
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
        int SelectedRowIndex = Table.getSelectedRow();
        
        //file
        fileCoppyPath = paths[SelectedRowIndex];
          System.out.println(fileCoppyPath.toString());
         
        
        tmpS = fileCoppyPath.toString().split("\\\\");
        String tmp = new String();
        for (int i=0;i<tmpS.length-1;i++)
        {
            tmp += tmpS[i] + "\\\\" ;
        }
        tmpF=tmpS[tmpS.length-1];
        tmp+=tmpF;
        System.out.println(tmp);
        fileCoppyPath = new File(tmp);
        String stringClipboard = "..\\clipboard\\"+tmpF;
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
        
        copy = false;
        cut = true;
    }//GEN-LAST:event_btnCutActionPerformed

    private void TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableMouseClicked
        // TODO add your handling code here:
        System.out.println("I'm in Table");
        jScrollPane2.requestFocus();
        JTable source = (JTable)evt.getSource();
        if (evt.getClickCount() == 2 && source.getSelectedRow() != -1)
        {
            int row = source.rowAtPoint( evt.getPoint() );
            int column = source.columnAtPoint( evt.getPoint() );
            String str=saveSelectedNode.toString()+"\\"+(String)source.getModel().getValueAt(row, column);
            File s=new File(str);
            //System.out.println("pathfile dang chon o table :"+s.getAbsolutePath());
            //System.out.println("Table clicked");
            Desktop desktop = Desktop.getDesktop();
            try{
                if(s.exists() && s.isFile()) desktop.open(s);
                else
                {
                    DefaultTableModel tableModel=(DefaultTableModel) Table.getModel();
                    paths=s.listFiles();
                    //System.out.println("fucksss : "+s.getAbsolutePath());
                    openingInTable=true;
                    tableIndex=row;
                    //System.out.println("Ban chon dong: "+tableIndex);
                  
                    TreeMouseClicked(evt);
                    //System.out.println("ddmmm cmmm");
                    //System.out.println("Path length :"+paths.length);
                    //ShowInTable(paths);
                }
            }
                    
            catch(Exception ex)
            {
                //System.out.println("Loi o day nay : "+ex.getMessage());
                //System.out.println("Table cos: "+Table.getRowCount());
                //for(File a:paths)
                    //System.out.println(a.getAbsolutePath());
                //ShowInTable(paths);
            }
        }
    }//GEN-LAST:event_TableMouseClicked

    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        // TODO add your handling code here:
        AboutForm aboutForm=new AboutForm();
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
                strCreateNode+=temp[i]+"\\";
                if(i>0) strCreateNode=strCreateNode.substring(0,strCreateNode.length()-1);
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
    }//GEN-LAST:event_btnBackMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int ck=JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa file này?", "Cảnh báo!", JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);
        if (ck==1) return;
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
        int SelectedRowIndex = Table.getSelectedRow();
        if(SelectedRowIndex==-1) return;
        //file
            File fileDelete = paths[SelectedRowIndex];
            if(fileDelete.isFile())
                fileDelete.delete();
            else
                try{
                    FileUtils.deleteDirectory(fileDelete);
                }
            catch(Exception e){
                
            }             
         loadTableWhenAction();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
       if(cut)
         {
            String stringClipboard = "..\\clipboard\\"+tmpF;
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
        Table.selectAll();
    }//GEN-LAST:event_itemSellectAllActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
      
    
    }//GEN-LAST:event_formKeyPressed

    private void TableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TableKeyPressed
        
    }//GEN-LAST:event_TableKeyPressed

    private void TreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TreeKeyPressed
        // TODO add your handling code here:
        
                        
        
        
    }//GEN-LAST:event_TreeKeyPressed

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
        // TODO add your handling code here:
        jScrollPane2.requestFocus();
        System.out.println("I'm in croll pane 2");
    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void jScrollPane2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane2KeyPressed
        // TODO add your handling code here:
        if ((evt.getKeyCode() == KeyEvent.VK_A)&&((evt.getModifiersEx()&KeyEvent.CTRL_DOWN_MASK)!=0)) {
                  Table.selectAll();
        }
    }//GEN-LAST:event_jScrollPane2KeyPressed

    private void itemFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemFolderActionPerformed
        // TODO add your handling code here:
        if(saveSelectedNode==null)return;
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
    }//GEN-LAST:event_btnRefreshActionPerformed
    
    
    void ShowInTable(File[] paths)
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
            if(paths[i].isDirectory())
            {
                row[0]=paths[i].getName();              
                Date d = new Date(paths[i].lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                String strDate = formatter.format(d);
                row[1]=strDate;
                row[2]="Folder";
                row[3]="N/A";
                tableModel.addRow(row);
            }
        for(int i=0;i<n;i++)
            if(paths[i].isFile())
            {
                row[0]=paths[i].getName();
                Date d = new Date(paths[i].lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   HH:mm:ss");
                String strDate = formatter.format(d);
                row[1]=strDate;
                row[2]="File";
                row[3]=paths[i].length()/1000+" KB";
                tableModel.addRow(row);
            }
        
        Table.getColumnModel().getColumn(0).setCellRenderer(new Render());
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
            //ThisPC.add(new DefaultMutableTreeNode(path));
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
        DefaultTableModel tableModel=(DefaultTableModel)Table.getModel();
        tableModel.addColumn("Name",name);
        tableModel.addColumn("Date modified",date_modified);
        tableModel.addColumn("Type",type);
        tableModel.addColumn("Size",size);
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
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnGoto;
    private javax.swing.JButton btnPaste;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUp;
    private javax.swing.JButton btnView;
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JMenuItem itemCopy;
    private javax.swing.JMenuItem itemCut;
    private javax.swing.JMenuItem itemDelete;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemFolder;
    private javax.swing.JMenu itemNew;
    private javax.swing.JMenuItem itemPaste;
    private javax.swing.JMenuItem itemRename;
    private javax.swing.JMenuItem itemSellectAll;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lbAddress;
    private javax.swing.JTextField textAddress;
    // End of variables declaration//GEN-END:variables

}

