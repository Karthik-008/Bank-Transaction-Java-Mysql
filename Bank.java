import java.io.*;

public class Bank {
    public static void main(String args[]) throws IOException {
        BufferedReader sc = new BufferedReader(
                new InputStreamReader(System.in));

        String name = "";
        int pass_code;
        int ch;

        while (true) {
            System.out.println(
                    "\n-> || Welcome To JavaBank ||<-\n");
            System.out.println("1.Create Account");
            System.out.println("2.Login Account");

            try {
                System.out.println("\n Enter input : ");
                ch = Integer.parseInt(sc.readLine());

                switch (ch) {
                    case 1:
                        try {
                            System.out.println("Enter unique username : ");
                            name = sc.readLine();
                            System.out.println("Enter new password : ");
                            pass_code = Integer.parseInt(sc.readLine());

                            if (BankManagement.createAccount(name, pass_code)) {
                                System.out.println("MSG : Account Created Successfully!\n");

                            } else {
                                System.out.println("ERR : Account Creation Failed!\n");
                            }
                        } catch (Exception e) {
                            System.out.println("Enter valid data :: Insertion Failed!\n ");
                        }
                        break;

                    case 2:
                        try {
                            System.out.println("Enter Username : ");
                            name = sc.readLine();
                            System.out.println("Enter password : ");
                            pass_code = Integer.parseInt(sc.readLine());

                            if (BankManagement.loginAccount(name, pass_code)) {
                                System.out.println("MSG : Logged Out Successfully!");

                            }

                            else {
                                System.out.println("ERR : Login Failed!\n");
                            }
                        } catch (Exception e) {
                            System.out.println("ERR : Enter valid data :: Login Failed!");

                        }
                        break;

                    default:
                        System.out.println("Invalid Entry!");
                }

                if (ch == 5) {
                    System.out.println("Exited Successfully!\n\nThank You");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Enter valid entry!");

            }
        }

        sc.close();
    }

}
