import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author K. Borzyszkowski
 */

class Konto implements Serializable {
    private final String imie;
    private final String nazwisko;
    private final String pin;
    private Double saldo;
    private final int id;
    Random random = new Random();



    public Konto(String imie, String nazwisko, String pin){
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.pin = pin;
        this.saldo = 0.0;
        this.id = random.nextInt(50) + 30;
    }

    public String getImie(){

        return this.imie;
    }
    public String getNazwisko(){

        return this.nazwisko;
    }
    public double getSaldo(){

        return this.saldo;
    }
    public void setSaldo(double noweSaldo) {

        this.saldo = noweSaldo;
    }
    public int getID(){

        return this.id;
    }
    public String getPin(){

        return this.pin;
    }

    public void przelewKredytu(double kredyt){
        this.saldo += kredyt;
        System.out.println("Na konto zosta³y przelane œrodki w wysokoœci " + kredyt + "\n" +
                "Saldo wynosi: " + this.saldo);
    }

    public void wplacPieniadze(double wplacanaKwota){
        if (wplacanaKwota < 0) {
            System.out.println("B³¹d! Podana wartoœæ nie mo¿e byæ ujemna!");
        } else {
            this.saldo += wplacanaKwota;
            System.out.println("Wp³aci³eœ " + wplacanaKwota + " na swoje konto." + "\n" +
                    "Saldo wynosi: " + this.saldo);
        }
    }

    public void wyplacPieniadze(double wyplacanaKwota){
        if (wyplacanaKwota < 0) {
            System.out.println("B³¹d! Wartoœæ nie mo¿e byæ ujemna!");}
        else {
            if (this.saldo < wyplacanaKwota) {
                System.out.println("Nie masz wystarczaj¹cych œrodków.");
            } else {
                this.saldo -= wyplacanaKwota;
                System.out.println("Wyp³acono " + wyplacanaKwota + " z twojego konta." + "\n" +
                        "Saldo wynosi: " + this.saldo);
            }
        }
    }


    public void przelew(Konto wybraneKonto, Konto doceloweKonto, double kwotaPrzelewana) {
        if (kwotaPrzelewana < 0) {
            System.out.println("B³¹d! Wartoœæ nie mo¿e byæ ujemna!");
        } else {
            if (wybraneKonto.getSaldo() > kwotaPrzelewana) {
                doceloweKonto.setSaldo(doceloweKonto.saldo += kwotaPrzelewana);
                wybraneKonto.setSaldo(this.saldo -= kwotaPrzelewana);
                System.out.println("Przekazano: " + kwotaPrzelewana);
            } else {
                System.out.println("Nie masz wystarczaj¹co du¿o œrodków!");
            }
        }
    }

    @Override
    public String toString(){
        return "Imiê: " + getImie() + "\n" +
                "Nazwisko: " + getNazwisko() +"\n" +
                "Saldo: " + getSaldo() + "\n" +
                "ID: " + getID();
    }
}

class Bank implements Serializable{

    private final ArrayList<Konto> kontaBankowe;
    private final Scanner sc;
    String filename = "accounts.txt";



    public Bank() throws IOException {
        kontaBankowe = new ArrayList<>();
        sc = new Scanner(System.in);
        File filename = new File("accounts.txt");

        if (filename.exists()) {
            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                @SuppressWarnings("unchecked")
                ArrayList<Konto> kontaBankowe2 = (ArrayList<Konto>) ois.readObject();
                kontaBankowe.addAll(kontaBankowe2);
                ois.close();
                fis.close();

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        } else {
            filename.createNewFile();

            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                @SuppressWarnings("unchecked")
                ArrayList<Konto> kontaBankowe2 = (ArrayList<Konto>) ois.readObject();
                kontaBankowe.addAll(kontaBankowe2);
                ois.close();
                fis.close();

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void zapiszDane() {

        try{
            FileOutputStream writeData = new FileOutputStream(filename);
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(kontaBankowe);
            writeStream.flush();
            writeStream.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Konto kontoIstnieje(int idKonta, String numerPin) {
        for (Konto konto : kontaBankowe) {
            if (konto.getID() == idKonta && konto.getPin().equals(numerPin)) {
                return konto;
            }
        } return null;
    }

    public Konto idIstnieje(int idKonta) {
        for (Konto konto : kontaBankowe) {
            if (konto.getID() == (idKonta)) {
                return konto;
            }
        } return null;
    }

    public String utworzKonto(String imie, String nazwisko, String pin) {

        if (pinPoprawny(pin)) {
            kontaBankowe.add(new Konto(imie, nazwisko, pin));
            zapiszDane();


        } return ("Konto utworzono poprawnie!\n U¿yj numeru ID oraz numeru PIN" + "\n" +
                "Numer ID konta: " + kontaBankowe.get(kontaBankowe.size() - 1).getID());
    }

    public void zalogowany(int idKonta, String numerPin) {
        Konto wybraneKonto = kontoIstnieje(idKonta, numerPin);
    }


    public void zaloguj() {
        System.out.println("Podaj numer ID: ");
        int idKonta = sc.nextInt();
        System.out.println("Podaj PIN: ");
        String numerPin = sc.next();
        if (pinPoprawny(numerPin)) {
            Konto wybraneKonto = kontoIstnieje(idKonta, numerPin);
            if (wybraneKonto == null) {
                System.out.println("Nie ma takiego konta!");
            } else {
                    zalogowany(idKonta, numerPin);
            }
        }
    }

    public static boolean pinPoprawny(String pin){
        if(pin.length() != 4){
            System.out.println("PIN musi zawieraæ 4 liczby.");
            return false;

        } else {
            return true;
        }
    }
}

class MainMenu extends JFrame implements ActionListener {

    JButton loginBtn, registerBtn, exitBtn;
    JLabel tlabel, slabel;

    private static boolean login = false;
    private static boolean register = false;

    public MainMenu() {
        setSize(350,300);
        setTitle("Witaj");
        setLayout(null);
        this.setLocation(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        tlabel = new JLabel("Witamy w BBANK");
        tlabel.setBounds(75,20,250,30);
        tlabel.setForeground(Color.DARK_GRAY);
        tlabel.setFont(new Font("SansSerif",Font.BOLD,25));
        add(tlabel);

        slabel = new JLabel("Wybierz opcjê: ");
        slabel.setBounds(110,70,200,30);
        slabel.setForeground(Color.DARK_GRAY);
        slabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(slabel);

        loginBtn = new JButton("Zaloguj");
        loginBtn.setBounds(110,100,120,30);
        add(loginBtn);
        loginBtn.addActionListener(this);

        registerBtn = new JButton("Utwórz konto");
        registerBtn.setBounds(110,140,120,30);
        add(registerBtn);
        registerBtn.addActionListener(this);

        exitBtn = new JButton("Zamknij");
        exitBtn.setBounds(110,180,120,30);
        add(exitBtn);
        exitBtn.addActionListener(this);

    }




    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src==exitBtn) {
            System.exit(0);
        } else if (src==loginBtn) {
            login = true;
//            dispose();
        } else if (src==registerBtn) {
            register = true;
//            dispose();
        }
    }

    public static boolean getLogin(){

        return login;
    }

    public static void setLoginFalse(){

        login = false;
    }

    public static boolean getRegister(){

        return register;
    }

    public static void setRegisterFalse(){
        register = false;
    }


}

class RegisterMenu extends JFrame implements ActionListener {

    JButton regBtn, backBtn;
    JLabel namelabel,surnlabel,idlabel,pinlabel,daneLabel, badlabel, pininflabel;
    JTextField nametxt, surnametxt, pintxt;
    int id;
    String pin,name,surname;
    public static boolean accept = false;
    public static boolean back = false;

    public RegisterMenu() {
        setSize(450,400);
        setTitle("Rejestracja");
        setLayout(null);
        this.setLocation(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        regBtn = new JButton("Utwórz konto");
        regBtn.setBounds(300,100,120,30);
        add(regBtn);
        regBtn.addActionListener(this);

        backBtn = new JButton("Powrót");
        backBtn.setBounds(300,140,120,30);
        add(backBtn);
        backBtn.addActionListener(this);

        namelabel = new JLabel("Imiê:");
        namelabel.setBounds(30,100,80,30);
        namelabel.setForeground(Color.DARK_GRAY);
        namelabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(namelabel);

        surnlabel = new JLabel("Nazwisko:");
        surnlabel.setBounds(30,140,120,30);
        surnlabel.setForeground(Color.DARK_GRAY);
        surnlabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(surnlabel);

        pinlabel = new JLabel("PIN:");
        pinlabel.setBounds(30,180,55,30);
        pinlabel.setForeground(Color.DARK_GRAY);
        pinlabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(pinlabel);

        pininflabel = new JLabel("Max. 4 cyfry");
        pininflabel.setBounds(180,180,150,30);
        pininflabel.setForeground(Color.DARK_GRAY);
        pininflabel.setFont(new Font("SansSerif",Font.PLAIN,12));
        add(pininflabel);

        badlabel = new JLabel("");
        badlabel.setBounds(60,200,120,30);
        badlabel.setFont(new Font("SansSerif",Font.BOLD,14));
        add(badlabel);





        daneLabel = new JLabel("Utwórz konto.");
        daneLabel.setBounds(40,60,250,30);
        daneLabel.setForeground(Color.DARK_GRAY);
        daneLabel.setFont(new Font("SansSerif",Font.BOLD,18));
        add(daneLabel);

        nametxt = new JTextField("");
        nametxt.setBounds(130,100,140,30);
        add(nametxt);

        surnametxt = new JTextField("");
        surnametxt.setBounds(130,140,140,30);
        add(surnametxt);

        pintxt = new JTextField((""));
        pintxt.setBounds(130,180,40,30);
        add(pintxt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (name!=null && pin!=null && surname!=null) {
            name=nametxt.getText();
            surname=surnametxt.getText();
            pin = pintxt.getText();
        }


        Object src = e.getSource();
        if (src==regBtn) {
            if (getPin().length() == 4){
                accept = true;
                badlabel.setText("Utworzono konto. Twój numer ID: ");
            } else {
                nametxt.setText("");
                surnametxt.setText("");
                pintxt.setText("");
                badlabel.setText("B³êdne dane");
            }

        } else if (src==backBtn) {
            back = true;
        }
    }

    public String getName(){
        return nametxt.getText();
    }

    public String getSurname(){
        return surnametxt.getText();
    }

    public String getPin(){
        return pintxt.getText();
    }



    public static boolean getAccept(){
        return accept;
    }
    public static boolean getBack(){
        return back;
    }

    public static void setBackFalse() {
        back = false;
    }

}

class LoginMenu extends JFrame implements ActionListener {

    private static Label badlabel;
    private static JTextArea infoTextArea;
    private JButton logBtn, backBtn;
    private JLabel idlabel,pinlabel,daneLabel, tlabel;
    private static JTextField idtxt, pintxt;
    private static int id;
    private String pin;
    private static boolean login = false;
    private static boolean back = false;

    public LoginMenu() {
        setSize(350,300);
        setTitle("logowanie");
        setLayout(null);
        this.setLocation(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        logBtn = new JButton("Zaloguj");
        logBtn.setBounds(200,100,120,30);
        add(logBtn);
        logBtn.addActionListener(this);

        backBtn = new JButton("Powrót");
        backBtn.setBounds(200,140,120,30);
        add(backBtn);
        backBtn.addActionListener(this);

        tlabel = new JLabel("Witamy w BBANK");
        tlabel.setBounds(75,20,250,30);
        tlabel.setForeground(Color.DARK_GRAY);
        tlabel.setFont(new Font("SansSerif",Font.BOLD,25));
        add(tlabel);

        idlabel = new JLabel("ID:");
        idlabel.setBounds(30,100,25,30);
        idlabel.setForeground(Color.DARK_GRAY);
        idlabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(idlabel);

        pinlabel = new JLabel("PIN:");
        pinlabel.setBounds(30,140,120,30);
        pinlabel.setForeground(Color.DARK_GRAY);
        pinlabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        add(pinlabel);

        badlabel = new Label("");
        badlabel.setBounds(20,150,250,80);
        badlabel.setFont(new Font("SansSerif",Font.BOLD,7));
//        add(badlabel);

        infoTextArea = new JTextArea(5,20);
        infoTextArea.setBounds(0,180,350,60);
        infoTextArea.setEditable(false);
        infoTextArea.setBackground(Color.lightGray);
        add(infoTextArea);

        daneLabel = new JLabel("Podaj dane, aby zalogowaæ.");
        daneLabel.setBounds(40,60,250,30);
        daneLabel.setForeground(Color.DARK_GRAY);
        daneLabel.setFont(new Font("SansSerif",Font.BOLD,18));
        add(daneLabel);

        idtxt = new JTextField("");
        idtxt.setBounds(60,100,120,30);
        add(idtxt);

        pintxt = new JTextField((""));
        pintxt.setBounds(60,140,120,30);
        add(pintxt);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (id!=0 && pin!=null) {
            id = Integer.parseInt(idtxt.getText());
            pin = pintxt.getText();
        }


        Object src = e.getSource();
        if (src==logBtn) {
            login = true;

        } else if (src==backBtn) {
            clear();
            back = true;
        }
    }

    public static Integer getID(){
        id = Integer.parseInt(idtxt.getText());
        return id;
    }

    public static String getPIN(){
        return pintxt.getText();
    }

    public static boolean getBack() {
        return back;
    }

    public static void getbackFalse() {
        back = false;
    }

    public static boolean getLogin(){

        return login;
    }

    public static void setLoginFalse(){

        login = false;
    }

    public static void setIncorrect() {
        infoTextArea.setText("Takie konto nie istnieje!");
    }

    public void setBadlabel(String txt){
        infoTextArea.setText(txt);
    }

    public static void clear(){
        idtxt.setText("");
        pintxt.setText("");
        infoTextArea.setText("");
    }
}

class LoggedMenu extends JFrame implements ActionListener  {


    private static JLabel underLabel, underlabel2, underlabel3, successlabel,saldolabel;
    private static JButton realizeDepositBtn, realizeWithdrawBtn, realizePrzelewBtn;
    JButton exitBtn, withdrawBtn, depositBtn, przelewBtn, logoutBtn;
    JLabel daneLabel, infolabel,namelabel,surnamelabel,idlabel;
    private static JTextField depositTxt, IDTxt;

    private static double saldo;
    private static boolean logout = false;
    private static boolean deposit = false;

    public LoggedMenu(int ID, String pin) throws IOException {
        setSize(400,400);
        setTitle("Menu");
        setLayout(null);
        this.setLocation(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(ID,pin);


        infolabel = new JLabel("ID: " + wybraneKonto.getID());
        infolabel.setBounds(30,80,200,40);
        infolabel.setBackground(Color.LIGHT_GRAY);
        infolabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        infolabel.setOpaque(true);
        add(infolabel);

        namelabel = new JLabel("Imiê: " + wybraneKonto.getImie());
        namelabel.setBounds(30,110,200,40);
        namelabel.setBackground(Color.LIGHT_GRAY);
        namelabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        namelabel.setOpaque(true);
        add(namelabel);

        surnamelabel = new JLabel("Nazwisko: " + wybraneKonto.getNazwisko());
        surnamelabel.setBounds(30,140,200,40);
        surnamelabel.setBackground(Color.LIGHT_GRAY);
        surnamelabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        surnamelabel.setOpaque(true);
        add(surnamelabel);

        saldolabel = new JLabel("Saldo: " + wybraneKonto.getSaldo() + " z³");
        saldolabel.setBounds(30,170,200,40);
        saldolabel.setBackground(Color.LIGHT_GRAY);
        saldolabel.setFont(new Font("SansSerif",Font.PLAIN,14));
        saldolabel.setOpaque(true);
        add(saldolabel);

        depositBtn = new JButton("Wp³ata");
        depositBtn.setBounds(250,80,120,30);
        add(depositBtn);
        depositBtn.addActionListener(this);

        withdrawBtn = new JButton("Wyp³ata");
        withdrawBtn.setBounds(250,120,120,30);
        add(withdrawBtn);
        withdrawBtn.addActionListener(this);

        przelewBtn = new JButton("Przelew");
        przelewBtn.setBounds(250,160,120,30);
        add(przelewBtn);
        przelewBtn.addActionListener(this);

        logoutBtn = new JButton("Wyloguj");
        logoutBtn.setBounds(250,200,120,30);
        add(logoutBtn);
        logoutBtn.addActionListener(this);

        exitBtn = new JButton("Zakoñcz");
        exitBtn.setBounds(250,280,120,30);
        add(exitBtn);
        exitBtn.addActionListener(this);


        daneLabel = new JLabel("Witaj " + wybraneKonto.getImie());
        daneLabel.setBounds(40,30,250,30);
        daneLabel.setForeground(Color.DARK_GRAY);
        daneLabel.setFont(new Font("SansSerif",Font.BOLD,18));
        add(daneLabel);

        underLabel = new JLabel("");
        underLabel.setBounds(30,210,200,30);
        underLabel.setFont(new Font("SansSerif",Font.BOLD,13));
        add(underLabel);

        depositTxt = new JTextField("");
        depositTxt.setBounds(30,240,200,30);
        add(depositTxt);
        depositTxt.setVisible(false);

        underlabel3 = new JLabel("");
        underlabel3.setBounds(30,280,250,20);
        add(underlabel3);
        underlabel3.setVisible(false);

        realizeDepositBtn = new JButton("Wp³aæ");
        realizeDepositBtn.setBounds(50,280,100,30);
        add(realizeDepositBtn);
        realizeDepositBtn.setVisible(false);
        realizeDepositBtn.addActionListener(this);

        realizeWithdrawBtn = new JButton("Wyp³aæ");
        realizeWithdrawBtn.setBounds(50,280,100,30);
        add(realizeWithdrawBtn);
        realizeWithdrawBtn.setVisible(false);
        realizeWithdrawBtn.addActionListener(this);

        realizePrzelewBtn = new JButton("Przelej");
        realizePrzelewBtn.setBounds(150,310,80,30);
        add(realizePrzelewBtn);
        realizePrzelewBtn.setVisible(false);
        realizePrzelewBtn.addActionListener(this);

        IDTxt = new JTextField("0");
        IDTxt.setBounds(30,310,80,30);
        add(IDTxt);
        IDTxt.setVisible(false);

        successlabel = new JLabel("");
        successlabel.setBounds(30,330,350,30);
        add(successlabel);
        successlabel.setVisible(false);

        underlabel2 = new JLabel("0");
        underlabel2.setBounds(30,310,125,20);
        add(underlabel2);
        underlabel2.setVisible(false);

    }

    public static void setUnderlabel2(String tekst) {
        underlabel2.setText(tekst);
    }


    public static void setDepositTxt() {
        underLabel.setText("Podaj kwotê wp³aty");
    }

    public static void setWithdrawTxt() {
        underLabel.setText("Podaj kwotê wyp³aty");
    }




    @Override
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();
        Bank bank = null;
        try {
            bank = new Bank();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert bank != null;
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());


        if (src==logoutBtn) {
            depositTxt.setVisible(false);

            dispose();
            logout = true;

        } else if (src==exitBtn) {
            System.exit(0);
        } else if (src==depositBtn) {
            realizeWithdrawBtn.setVisible(false);
            realizePrzelewBtn.setVisible(false);
            IDTxt.setVisible(false);
            underlabel3.setVisible(false);
            successlabel.setVisible(false);
            depositTxt.setVisible(true);
            realizeDepositBtn.setVisible(true);
            setDepositTxt();
        } else if(src==realizeDepositBtn) {
            try {
                doDeposit(Integer.parseInt(depositTxt.getText()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if(src==withdrawBtn) {
            realizeDepositBtn.setVisible(false);
            realizePrzelewBtn.setVisible(false);
            IDTxt.setVisible(false);
            underlabel3.setVisible(false);
            successlabel.setVisible(false);
            depositTxt.setVisible(true);
            realizeWithdrawBtn.setVisible(true);
            setWithdrawTxt();
        } else if(src==realizeWithdrawBtn) {
            try {
                doWithdraw(Integer.parseInt(depositTxt.getText()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if(src==przelewBtn) {
            depositTxt.setText("0");
            underLabel.setText("Jak¹ kwotê chcesz przelaæ?");
            underLabel.setVisible(true);
            depositTxt.setVisible(true);
            realizeWithdrawBtn.setVisible(false);
            realizeDepositBtn.setVisible(false);
            successlabel.setVisible(false);
            realizePrzelewBtn.setVisible(true);
            underlabel3.setVisible(true);
            underlabel3.setText("Podaj ID konta docelowego");
            IDTxt.setVisible(true);

        } else if(src==realizePrzelewBtn) {
            try {
                doPrzelew((Integer.parseInt(depositTxt.getText())),(Integer.parseInt(IDTxt.getText())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void setSaldoAfterW(int kwota) throws IOException {
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());
        saldo = wybraneKonto.getSaldo() - Double.parseDouble(String.valueOf(kwota));
        saldolabel.setText("Saldo: " + saldo + " z³");
    }

    public static void setSaldoAfterD(int kwota) throws IOException {
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());
        saldo = wybraneKonto.getSaldo() + Double.parseDouble(String.valueOf(kwota));
        saldolabel.setText(String.valueOf("Saldo: " + saldo + " z³"));
    }


    public static void clear(){
        depositTxt.setText("");
    }

    public static void doDeposit(int kwota) throws IOException {
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());

        if(kwota<=0) {
            underlabel2.setVisible(true);
            setUnderlabel2("B³êdna kwota");
        } else {
            wybraneKonto.wplacPieniadze(kwota);
            setSaldoAfterD(kwota);
            clear();
            bank.zapiszDane();
        }

    }

    public static void doWithdraw(int kwota) throws IOException {
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());


        if(kwota>wybraneKonto.getSaldo()) {
            underlabel2.setVisible(true);
            setUnderlabel2("Posiadasz za ma³o œrodków!");
        } else if(kwota<0) {
            underlabel2.setVisible(true);
            setUnderlabel2("B³êdna wartoœæ!");
        } else {
            wybraneKonto.wyplacPieniadze(kwota);
            setSaldoAfterW(kwota);
            setUnderlabel2("Wyp³acono: " + kwota + " z³");
            underlabel2.setVisible(true);
            clear();
            bank.zapiszDane();
        }

    }

    public static void doPrzelew(int kwota, int ID) throws IOException {
        Bank bank = new Bank();
        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(),LoginMenu.getPIN());
        Konto doceloweKonto = bank.idIstnieje(ID);



        if(kwota<0){
            successlabel.setVisible(true);
            successlabel.setText("B³êdna kwota");
        } else if(bank.idIstnieje(ID)==null) {
            successlabel.setVisible(true);
            successlabel.setText("Nie znaleziono konta o tym numerze ID");
        } else {
            wybraneKonto.wyplacPieniadze(kwota);
            doceloweKonto.wplacPieniadze(kwota);
            setSaldoAfterW(kwota);
            setUnderlabel2("Przelano: " + kwota + " z³");
            underlabel2.setVisible(true);
            successlabel.setText("Pomyœlnie przelano " + depositTxt.getText() + " z³ na konto ID: " + IDTxt.getText());
            successlabel.setVisible(true);
            IDTxt.setText("");
            clear();
            bank.zapiszDane();
        }
    }

    public static void setLogoutFalse() {
        logout = false;
    }

    public static boolean getLogout() {
        return logout;
    }


}

public class AplikacjaBankowa extends JFrame {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Bank bank = new Bank();

        final String ACTION_IDLE = "55";
        final String ACTION_LOGGED = "99";
        final String ACTION_MAIN = "0";
        final String ACTION_REG = "1";

        String action;

        RegisterMenu registerMenu = new RegisterMenu();
        MainMenu mainMenu = new MainMenu();
        LoginMenu loginMenu = new LoginMenu();


        action = ACTION_MAIN;


        while (true) {
            switch (action) {
                case ACTION_MAIN: {
                    mainMenu.setVisible(true);
                    registerMenu.setVisible(false);
                    loginMenu.setVisible(false);

                    if (MainMenu.getLogin()) {
                        action = ACTION_IDLE;
                        MainMenu.setLoginFalse();

                    }
                    if (MainMenu.getRegister()) {
                        action = ACTION_REG;
                        MainMenu.setRegisterFalse();
                    }
                    break;
                }
                case ACTION_IDLE: {
                    mainMenu.setVisible(false);
                    registerMenu.setVisible(false);
                    loginMenu.setVisible(true);


                    if (LoginMenu.getLogin()) {
                        Konto wybraneKonto = bank.kontoIstnieje(LoginMenu.getID(), LoginMenu.getPIN());

                        if (wybraneKonto == null) {
                            LoginMenu.setIncorrect();
                            LoginMenu.setLoginFalse();
                        } else {
                            LoggedMenu loggedMenu = new LoggedMenu(LoginMenu.getID(),LoginMenu.getPIN());
                            loggedMenu.setVisible(true);
                            action = ACTION_LOGGED;
                            LoginMenu.setLoginFalse();
                        }
                    }

                    if (LoginMenu.getBack()) {
                        action = ACTION_MAIN;
                        LoginMenu.getbackFalse();
                    }
                    break;
                }
                case ACTION_LOGGED: {
                    mainMenu.setVisible(false);
                    registerMenu.setVisible(false);
                    loginMenu.setVisible(false);

                    if(LoggedMenu.getLogout()) {
                        LoginMenu.clear();
                        action = ACTION_IDLE;
                        LoggedMenu.setLogoutFalse();
                    }


                } break;

                case ACTION_REG: {
                    mainMenu.setVisible(false);
                    registerMenu.setVisible(true);
                    loginMenu.setVisible(false);


                    if(RegisterMenu.getAccept()) {
                        if(Bank.pinPoprawny(registerMenu.getPin())) {
                            String wiadomosc = bank.utworzKonto(registerMenu.getName(),registerMenu.getSurname(),registerMenu.getPin());
                            loginMenu.setBadlabel(wiadomosc);
                            System.out.println(wiadomosc);
                            action = ACTION_IDLE;
                        }
                    }

                    if (RegisterMenu.getBack()) {
                            LoginMenu.clear();
                            action = ACTION_MAIN;
                            RegisterMenu.setBackFalse();
                        }
                }
                break;
            }
        }
    }
}
