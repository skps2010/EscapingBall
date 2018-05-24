package ball;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class create extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	JDialog d=new JDialog();
	JTextField text1 = new JTextField(10);
	JTextField text2 = new JTextField(10);
	JTextField text3 = new JTextField(10);
	JLabel label1=new JLabel("名稱：");
	JLabel label2=new JLabel("　寬：");
	JLabel label3=new JLabel("　高：");
    JButton button = new JButton("確定");
    Level newlevel;
    
    create()
    {
    	setSize(200, 150);
		setVisible(true);
		setLocationRelativeTo(null);
    	setLayout(new GridLayout(4, 1));
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
    	setAlwaysOnTop(true);
    	
    	button.addActionListener(this);
    	JPanel panel1=new JPanel();
    	JPanel panel2=new JPanel();
    	JPanel panel3=new JPanel();
    	panel1.add(label1);
    	panel1.add(text1);
    	panel2.add(label2);
    	panel2.add(text2);
    	panel3.add(label3);
    	panel3.add(text3);
    	getContentPane().add(panel1);
    	getContentPane().add(panel2);
    	getContentPane().add(panel3);
    	getContentPane().add(button);
    	setVisible(true);
    }
    
	public void actionPerformed(ActionEvent event) {
		try 
		{
			int x=Integer.parseInt(text2.getText());	
			int y=Integer.parseInt(text3.getText());
			char[] level=new char[x*y];
			
			if(x<2||x>20||y<2||y>20)throw new Exception();
			for(int i=0;i<level.length;i++)
			{
				level[i]='0';
			}
			newlevel=new Level(text1.getText(),x,y,String.valueOf(level));
			dispose();
			
			
        }
        catch (Exception ex) {
        	JOptionPane.showMessageDialog(this,"請在寬和高的欄位輸入2～20");
        }
		
		
    }
}
