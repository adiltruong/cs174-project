// try {
//     ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId));
//     if (rs.next()){
//         if(balTooLow(accountId, amount)){
//             return "1";
//         }
//         stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(accountId));
//         stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'withdraw', '"+generateRandomChars(9)+"', "+parse(accountId)+", NULL)");
//     }
//     closeAccountBalanceCheck(accountId);
// } catch(Exception e) {
//     System.out.println(e);
//     return "1";
// }