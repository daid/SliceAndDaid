package daid.sliceAndDaid.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import daid.sliceAndDaid.SliceAndDaidMain;
import daid.sliceAndDaid.config.CraftConfig;
import daid.sliceAndDaid.config.CraftConfigLoader;
import daid.sliceAndDaid.config.Setting;

public class ConfigWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private JPanel configSettingsPanel;
	private JPanel actionPanel;
	
	public ConfigWindow()
	{
		this.setTitle("SliceAndDaid - " + CraftConfig.VERSION);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.configSettingsPanel = new JPanel(new GridBagLayout());
		this.actionPanel = new JPanel(new GridBagLayout());
		this.add(configSettingsPanel);
		this.add(actionPanel, BorderLayout.SOUTH);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		JButton sliceButton = new JButton("Slice");
		sliceButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter()
				{
					public boolean accept(File f)
					{
						if (f.isDirectory())
							return true;
						return f.getName().endsWith(".stl");
					}
					
					public String getDescription()
					{
						return null;
					}
					
				});
				fc.setSelectedFile(new File(CraftConfig.lastSlicedFile));
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					SliceAndDaidMain.sliceModel(fc.getSelectedFile().toString());
				}
			}
		});
		this.actionPanel.add(sliceButton, c);
		
		final JComboBox levelSelect = new JComboBox(new String[] { "Starter", "Normal", "Advance" });
		levelSelect.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				CraftConfig.showLevel = levelSelect.getSelectedIndex();
				try
				{
					CraftConfigLoader.saveConfig(null);
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				createConfigFields(CraftConfig.showLevel);
			}
		});
		levelSelect.setSelectedIndex(CraftConfig.showLevel);
		this.actionPanel.add(levelSelect, c);
		
		createConfigFields(CraftConfig.showLevel);
		this.setVisible(true);
	}
	
	private void createConfigFields(int minShowLevel)
	{
		configSettingsPanel.removeAll();
		
		Class<CraftConfig> configClass = CraftConfig.class;
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.ipadx = 10;
		for (final Field f : configClass.getFields())
		{
			Setting s = f.getAnnotation(Setting.class);
			Object obj = null;
			
			try
			{
				obj = f.get(null).toString();
				
				if (s == null || obj == null)
					continue;
				if (s.level() > minShowLevel)
					continue;
				Component comp = null;
				Class<?> type = f.getType();
				if (type == Integer.TYPE)
				{
					JSpinner spinner = new JSpinner(new SpinnerNumberModel(f.getInt(null), (int) s.minValue(), (int) s.maxValue(), 1));
					spinner.addChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							try
							{
								f.setInt(null, ((Integer) ((JSpinner) e.getSource()).getValue()).intValue());
								CraftConfigLoader.saveConfig(null);
							} catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
					});
					comp = spinner;
				} else if (type == Double.TYPE)
				{
					JSpinner spinner = new JSpinner(new SpinnerNumberModel(f.getDouble(null), s.minValue(), s.maxValue(), 0.01));
					spinner.addChangeListener(new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							try
							{
								f.setDouble(null, ((Double) ((JSpinner) e.getSource()).getValue()).doubleValue());
								CraftConfigLoader.saveConfig(null);
							} catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
					});
					comp = spinner;
				} else
				{
					System.out.println("Unknown field type for config: " + type);
				}
				if (comp == null)
					continue;
				
				comp.setPreferredSize(new Dimension(100, 25));
				JLabel label = new JLabel(s.title() + ":");
				label.setToolTipText(s.description());
				c.gridx = 0;
				configSettingsPanel.add(label, c);
				c.gridx = 1;
				configSettingsPanel.add(comp, c);
				c.gridy++;
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
