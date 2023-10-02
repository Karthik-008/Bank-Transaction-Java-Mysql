import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class BankManagement {
    private static final int NULL = 0;

    static Connection con = Connect.getConnection();
    static String sql = "";

    public static boolean createAccount(String name, int passcode) {
        try {
            if (name == "" || passcode == NULL) {
                System.out.println("All Field Required!");
                return false;
            }

            Statement st = con.createStatement();
            sql = "insert into customer(cname,balance,pass_code) values('" + name + "',1000," + passcode + ")";

            if (st.executeUpdate(sql) == 1) {
                System.out.println(name + ", Now You can Login!");
                return true;
            }
        }

        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username not available!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean loginAccount(String name, int passCode) {
        try {
            if (name == "" || passCode == NULL) {
                System.out.println("All Field Required!");
                return false;
            }

            sql = "select * from customer where cname = '" + name + "' and pass_code = " + passCode;
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));

            if (rs.next()) {
                int ch = 5;
                int amt = 0;
                int senderAc = rs.getInt("ac_no");
                int receiverAc;

                while (true) {
                    try {
                        System.out.println("Hello " + rs.getString("cname"));
                        System.out.println("1.Transfer Money");
                        System.out.println("2.View Balance");
                        System.out.println("3.Deposit");
                        System.out.println("4.Withdraw");
                        System.out.println("5s.Logout");

                        System.out.println("Enter choice : ");
                        ch = Integer.parseInt(sc.readLine());

                        if (ch == 1) {
                            System.out.println("Enter receiver A/c no : ");
                            receiverAc = Integer.parseInt(sc.readLine());
                            System.out.println("Enter amount : ");
                            amt = Integer.parseInt(sc.readLine());

                            if (BankManagement.transferMoney(senderAc, receiverAc, amt)) {
                                System.out.println("MSG : Money Sent Successfully!\n");
                            } else {
                                System.out.println("ERR : Failed!\n");
                            }
                        }

                        else if (ch == 2) {
                            BankManagement.getBalance(senderAc);
                        }

                        else if (ch == 3) {
                            System.out.println("Enter deposit amount : ");
                            amt = Integer.parseInt(sc.readLine());
                            if (BankManagement.depositMoney(senderAc, amt)) {
                                System.out.println("MSG : Money deposited!\n");
                            } else {
                                System.out.println("ERR : Failed!\n");
                            }
                        }

                        else if (ch == 4) {
                            System.out.println("Enter withdraw amount : ");
                            amt = Integer.parseInt(sc.readLine());

                            if (BankManagement.withdrawMoney(senderAc, amt)) {
                                System.out.println("MSG : Money withdrawn\n");
                            } else {
                                System.out.println("ERR : Failed!\n");
                            }
                        }

                        else if (ch == 5) {
                            break;
                        }

                        else {
                            System.out.println("ERR : Enter valid Input!\n");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

            } else {
                return false;
            }

            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("User not available!");
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void getBalance(int acno) {
        try {
            sql = "select * from customer where ac_no = " + acno;
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            System.out.println("---------------------------------------------------");
            System.out.println("Account No\t\tName\t\tBalance");

            while (rs.next()) {
                System.out.printf("%d\t\t\t%s\t\t%d\n", rs.getInt("ac_no"),
                        rs.getString("cname"),
                        rs.getInt("balance"));
                System.out.println("----------------------------------------------------");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean transferMoney(int sender_ac, int receiver_ac, int amount) throws SQLException {
        if (receiver_ac == NULL || amount == NULL) {
            System.out.println("All Fields Required!");
            return false;
        }

        try {
            con.setAutoCommit(false);
            sql = "select * from customer where ac_no=" + sender_ac;
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("balance") < amount) {
                    System.out.println("Insufficient Balance!");
                    return false;
                }
            }
            Statement st = con.createStatement();

            con.setSavepoint();

            // Deduct from sender.....

            sql = "update customer set balance=balance-" + amount + " where ac_no=" + sender_ac;
            if (st.executeUpdate(sql) == 1) {
                System.out.println("Amount Debited!");

            }

            sql = "update customer set balance=balance+" + amount + " where ac_no=" + receiver_ac;
            st.executeUpdate(sql);

            con.commit();
            return true;

        }

        catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }

        return false;
    }

    public static boolean depositMoney(int acno, int amount) throws SQLException {
        if (amount == NULL || acno == NULL) {
            System.out.println("All fields required!");
            return false;
        }

        try {
            con.setAutoCommit(false);

            Statement st = con.createStatement();
            con.setSavepoint();

            sql = "update customer set balance = balance + " + amount + " where ac_no = " + acno;
            st.executeUpdate(sql);

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
        return false;
    }

    public static boolean withdrawMoney(int acno, int amount) throws SQLException {
        if (amount == NULL || acno == NULL) {
            System.out.println("All Fields Required!");
            return false;
        }

        try {
            con.setAutoCommit(false);

            sql = "select * from customer where ac_no = " + acno;
            PreparedStatement st = con.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                if (rs.getInt("balance") < amount) {
                    System.out.println("Insufficient Balance!");
                    return false;
                }
            }

            con.setSavepoint();

            Statement st1 = con.createStatement();
            sql = "update customer set balance = balance - " + amount + " where ac_no = " + acno;

            st1.executeUpdate(sql);

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }

        return false;
    }

}
