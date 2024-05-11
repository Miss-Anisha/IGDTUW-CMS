package cms;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

public class compFile {
	private String compFName;
	int totalComps;
	private List<Complaints> compList;

	public compFile(String compFName) {
		this.compFName = compFName;
		initList();
		this.totalComps = compList.size();
	}

	private void initList() {
		compList = new ArrayList<>();
		File f = new File(compFName);
		if (f.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(compFName));
				while (true) {
					compList.add((Complaints) ois.readObject());
				}
			} catch (EOFException eof) {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addComp(Complaints comp) {
		compList.add(comp);
		this.totalComps++;
	}

	public void addSoln(int cNo, String soln) throws Exception {
		addSoln(cNo, soln, false);
	}

	public void overwriteSoln(int cNo, String soln) {
		try {
			addSoln(cNo, soln, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addSoln(int cNo, String soln, boolean overWrite) throws Exception {
		Complaints comp = getComp(cNo);
		if (comp.soln.isEmpty() || overWrite) {
			Complaints newComp = new Complaints(comp.cNo, comp.dept, comp.comp, soln);
			remove(comp);
			addComp(newComp);
		} else if (!comp.soln.isEmpty()) {
			throw new Exception("Solution Already Exists");
		}
	}

	public String getSoln(int cNo) {
		Complaints comp = getComp(cNo);
		if (comp != null) {
			return comp.soln;
		}
		return null;
	}

	public boolean findComp(int cNo) {
		return getComp(cNo) != null;
	}

	private Complaints getComp(int cNo) {
		for (Complaints comp : compList) {
			if (comp.cNo == cNo) {
				return comp;
			}
		}
		return null;
	}

	private void remove(Complaints compTbr) {
		compList.remove(compTbr);
	}

	public JTable returnTable() {
		JTable table;
		Object columnNames[] = { "C.No.", "Department", "Complaint", "Solution" };
		Object rowData[][] = new Object[totalComps][columnNames.length];
		int i = 0;
		for (Complaints comp : compList) {
			rowData[i][0] = comp.cNo;
			rowData[i][1] = comp.dept;
			rowData[i][2] = comp.comp;
			rowData[i][3] = comp.soln;
			++i;
		}
		table = new JTable(rowData, columnNames);
		return table;
	}

	public void exit() {
		try {
			FileWriter fw = new FileWriter(compFName);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(compFName));
			oos.flush();
			for (Complaints comp : compList) {
				oos.writeObject(comp);
				oos.flush();
			}
			oos.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}