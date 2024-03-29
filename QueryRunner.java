/*
 * Group 3
 * CPSC 5021, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package queryrunner;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC
 * class which will enable MYSQL queries to be executed. It also has functions
 * to provide the returned data from the Queries. Currently the eventHandlers in
 * QueryFrame call these functions in order to run the Queries.
 *
 * @author mckeem, hadley, cooper, li
 */
public class QueryRunner {

    /**
     * Instantiates a new query runner.
     */
    public QueryRunner() {
        this.jdbcData = new QueryJDBC();
        updateAmount = 0;
        queryArray = new ArrayList<>();
        error="";
        
        this.projectTeamApplication="ADTRACKER";

        // PRODUCT QUERIES
        // 1. Allows users to catch a glimpse of the top 5 rated products in a given
        // category User input: outdoors, electronics, clothing
        queryArray.add(new QueryData(
              "Top 5 rated products in a given category.\n\n"
            + "Please enter category from:\n\"outdoors\", \"electronics\", or \"clothing\"\n",
            "SELECT " +
                "P.product_id, product_name, seller_name,\n\t" +
                "product_description as description, product_price as price,\n\t" +
                "product_rating as rating, product_reviews as reviews,\n\t" +
                "C.manager_id, campaign_id\n" +
            "FROM Product P Join Seller USING (seller_id) Join Campaign C USING (seller_id)\n" +
            "WHERE product_description LIKE CONCAT('%', ?, '%')\n" +
            "ORDER BY P.product_rating DESC, P.product_reviews DESC\n" +
            "LIMIT 5",
            new String[] {"Product Category"}, new boolean [] {true},
            false, true));

        // 2. Search for a product.
        queryArray.add(new QueryData("Search for a product to see product overview.\n"
        		+ "(Product name format: brand name + category).\n\n"
        		+ "To see a specific product,\nenter a partial brand name keyword.\n\n"
        		+ "To see an overview of a certain category,\nenter a category name.",
            "SELECT " +
                "seller_name, product_name, product_rating, \n\t" +
                "product_reviews product_price \n" +
            "FROM Seller \n" +
            "JOIN Product USING (seller_id) \n" +
            "WHERE product_name LIKE CONCAT('%', ?, '%') \n" +
            "ORDER BY seller_name, product_name;",
            new String [] {"Product"}, new boolean [] {true},
            false, true));

        // 3. Overview of product performance by seller.
        queryArray.add(new QueryData(
            "Overview of product performance by seller.",
            "SELECT " +
                "seller_name, round(avg(product_rating), 1) AVGRATING, \n\t" +
                "round(avg(product_reviews), 0) AVGREVIEWS, \n\t" +
                "round(avg(product_price), 2) AVGPRICE \n" +
            "FROM Seller \n" +
            "JOIN Product USING (seller_id) \n" +
            "GROUP BY seller_id \n" +
            "ORDER BY AVGRATING desc, AVGREVIEWS desc, AVGPRICE;",
            null, null, false, true));
        
        

        // 4. Insert new product.
        queryArray.add(new QueryData(
            "Insert new product.",
            "INSERT INTO Product \n\t" +
                "(product_name, seller_id, product_description, " +
                "product_price)\n" +
            "VALUES (?,?,?,?);",
            new String [] {"Product Name", "Seller ID",
                "Product Description", "Product Price"},
            new boolean [] {false, false, false, false},
            true, true));


        
        // 5.Overview of top 5 performing managers by clicks. 
        queryArray.add(new QueryData(
            "Overview of top performing managers by product\nclicks.",
            "Select " +
                "manager_id, manager_first_name, manager_last_name, \n\t" +
                "campaign_id, campaign_name, campaign_clicks \n" +
            "FROM Account_Manager \n" +
            "JOIN Campaign USING (manager_id)\n" +
            "JOIN Campaign_Performance USING (campaign_id) \n" +
            "WHERE campaign_clicks > (Select avg(campaign_clicks) \n\t\t\t\t\t\t " +
                                  "FROM Campaign_Performance \n\t\t\t\t\t\t " +
                                  "GROUP BY manager_ID) \n" +
            "ORDER BY campaign_clicks DESC",
            null, null, false, false));

        
        // 6. Overview of top performing ad campaigns and ad groups. 
        queryArray.add(new QueryData(
            "Overview of top performing ad campaigns\nand ad groups.",
            "SELECT " +
                "campaign_id, campaign_name, ad_group_name, \n\t" +
                "ad_group_impressions as impressions, \n\t" +
                "ad_group_clicks as clicks, ad_group_cpc as cpc, \n\t" +
                "ad_group_spends as spends, ad_group_sales as sales, \n\t" +
                "ad_group_orders as orders, \n\t" +
                "round((ad_group_orders / ad_group_clicks)*100, 2) as \"conv rate(%)\", \n\t" +
                "ad_group_acos as ACOS, ad_group_roas as ROAS \n" +
            "FROM Campaign \n" +
            "JOIN Ad_Group USING(campaign_id) \n" +
            "JOIN Ad_Group_Performance USING(ad_group_id) \n" +
            "WHERE ad_group_acos < 0.3 or ad_group_roas > 0.5 \n" +
            "ORDER BY ad_group_acos, ad_group_id;",
    	    null, null, false, false));
        
        
        // 7. Overview of top performing ad campaigns and ad groups. (User input: ACOS <, ROAS >)
        queryArray.add(new QueryData(
            "Overview of top performing ad campaigns\nand ad groups by ACOS and ROAS.\n\n"
            + "Please enter decimal number between 0.1-1 to filter\n"
            + "performance whose ACOS < input, ROAS > input\n"
            + "**ACOS-Ads spends/Sales,the lower the better.\n"
            + "**ROAS-Sales/Ad spends,the higher the better.\n",
            "SELECT " +
                 "campaign_id, campaign_name, ad_group_name, \n\t" +
                 "ad_group_impressions as impressions, \n\t" +
                 "ad_group_clicks as clicks, ad_group_cpc as cpc, \n\t" +
                 "ad_group_spends as spends, ad_group_sales as sales, \n\t" +
                 "ad_group_orders as orders, \n\t" +
                 "round((ad_group_orders / ad_group_clicks)*100, 2) " +
                 "as \"conv rate(%)\", \n\t" +
                 "ad_group_acos as ACOS, ad_group_roas as ROAS \n" +
            "FROM Campaign \n" +
            "JOIN Ad_Group USING (campaign_id) \n" +
            "JOIN Ad_Group_Performance USING (ad_group_id) \n" +
            "WHERE ad_group_acos < ? AND ad_group_roas > ? \n" +
            "ORDER BY ad_group_acos, ad_group_id;",
            new String [] {"ACOS", "ROAS"}, new boolean [] {false, false},
            false, true));
        
         // 8. Allow user to search for open ad groups and ad group name 
         // containing the name of a targeted product and ad group type (e.g. 
         // User input: ad group name: tent, keyboard, shirt 
         //             ad group type (sponsored): brand, product
        queryArray.add(new QueryData(
            "Search for ad groups and ad group name containing\nthe name " +
                 "of a targeted product and ad group type.",
            "SELECT " +
                 "ad_group_id, ad_group_name, ad_group_start, \n\t" +
                 "ad_group_end, ad_group_impressions as impressions, \n\t" +
                 "ad_group_clicks as clicks, ad_group_cpc as cpc, \n\t" +
                 "ad_group_ctr as 'ctr(%)', ad_group_sales as sales, \n\t" +
                 "ad_group_spends as spends, ad_group_acos AS ACOS, \n\t" +
                 "ad_group_roas as ROAS \n" +
            "FROM Ad_Group \n" +
            "JOIN Ad_Group_Performance USING (ad_group_id) \n" +
            "WHERE ad_group_name LIKE CONCAT('%', ?, '%') \n\t" +
                "AND ad_group_type LIKE CONCAT('%', ?, '%') \n" +
            "HAVING ad_group_end IS NULL \n" +
            "ORDER BY sales DESC",
            new String [] {"Ad Group Name", "Ad Group Type"},
            new boolean [] {true, true}, false, true));

        // 9. Top performing keyword(click through rate > 0.4 , acos < 0.7, roas > 0.4))
        queryArray.add(new QueryData(
            "Top performing keyword.\n\n"
            + "Click through rate > 0.4\n"
            + "ACOS < 0.7\nROAS > 0.4",
            "SELECT " +
                 "ad_group_name, ad_group_budget, \n\t" +
                 "keyword, keyword_impressions as impressions, \n\t" +
                 "keyword_clicks as clicks , keyword_ctr as 'ctr(%)', \n\t" +
                 "keyword_cpc as cpc, keyword_orders as orders, \n\t" +
                 "round((keyword_orders / keyword_clicks)*100, 2) " +
                 "as 'conv rate(%)', \n\t" +
                 "keyword_spends as spends , keyword_sales as sales, \n\t" +
                 "keyword_acos as ACOS , keyword_roas as ROAS \n" +
            "FROM Keyword \n" +
            "JOIN Ad_Group USING (ad_group_id) \n" +
            "JOIN Keyword_Performance USING (keyword_id) \n" +
            "WHERE keyword_ctr > 0.4 AND keyword_acos < 0.7 AND keyword_roas > 0.4 \n" +
            "ORDER BY keyword_acos asc;",
            null, null, false, false));
    
        // 10. Top performing ads groups with sales greater than average.
        // User input: campaign_name(includes special strategy-competitor, defensive, generic...)
        queryArray.add(new QueryData(
            "Top performing ads groups with sales greater than\naverage sales.\n\n"
            + "Part of campaign names indicate campaign strategy\nwith "
            + "\"competitor\",  \"defensive\",  \"generic\" keywords\n",
            "SELECT " +
                 "c.campaign_id, c.campaign_name, a.ad_group_start, \n\t" +
                 "a.ad_group_name, p.product_name, \n\t" +
                 "p.product_description as 'prod descript', " +
                 "p.product_price as price, pf.ad_group_orders as orders, \n\t" +
                 "pf.ad_group_sales as sales, \n\t" +
                 "round((ad_group_sales / ad_group_orders), 0) as 'sales unit' \n" +
            "FROM Campaign c \n" +
            "JOIN Ad_Group a ON c.campaign_id = a.campaign_id \n" +
            "JOIN Ad_Group_Performance pf ON a.ad_group_id = pf.ad_group_id \n" +
            "JOIN Product p ON c.product_id = p.product_id \n" +
            "WHERE campaign_name LIKE CONCAT('%', ?, '%') \n\t" +
                "AND ad_group_sales > (SELECT avg(ad_group_sales) as 'avg sales' " +
        	                    "\n   \t\t\t\t\t\tFROM Ad_Group_Performance) \n" +
            "ORDER BY ad_group_sales desc;",
            new String [] {"Campaign Name"}, new boolean [] {true},
            false, true));
    }

    /**
     * Gets total number of queries.
     *
     * @return total
     */
    public int GetTotalQueries() {
        return queryArray.size();
    }

    /**
     * Gets the parameter amount for a query.
     *
     * @param queryChoice the query choice
     * @return the index of the query in the array
     */
    public int GetParameterAmtForQuery(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.GetParmAmount();
    }

    /**
     * Gets the parameter text.
     *
     * @param queryChoice the query choice
     * @param paramNum the parmnum
     * @return the string
     */
    public String GetParamText(int queryChoice, int paramNum) {
       QueryData e = queryArray.get(queryChoice);
       return e.GetParamText(paramNum);
    }

    /**
     * Gets the query text.
     *
     * @return the string.
     */
    public String GetQueryText(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.GetQueryString();        
    }

    /**
     * Gets the query description.
     *
     * @return the string.
     */
    public String GetQueryTitle(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.GetTitle();
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query.
     *
     * @return Returns how many rows were updated
     */
    public int GetUpdateAmount() {
        return updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query.
     *
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders() {
        return jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format.
     *
     * @return multi-dimensional array of String data based on the resultset
     * from the query
     */
    public String[][] GetQueryData() {
        return jdbcData.GetData();
    }

    /**
     * Get title of application.
     *
     * @return title.
     */
    public String GetProjectTeamApplication() {
        return projectTeamApplication;
    }

    /**
     * Get whether query is action (insert or update).
     *
     * @param queryChoice
     * @return true or false.
     */
    public boolean isActionQuery (int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.IsQueryAction();
    }

    /**
     * Checks if is parameter query.
     *
     * @param queryChoice the query choice
     * @return true, if is parameter query
     */
    public boolean isParameterQuery(int queryChoice) {
        QueryData e = queryArray.get(queryChoice);
        return e.IsQueryParm();
    }

    /**
     * Execute query.
     *
     * @param queryChoice the query choice
     * @param params the parms
     * @return true, if successful
     */
    public boolean ExecuteQuery(int queryChoice, String [] params) {
        boolean bOK;
        QueryData e = queryArray.get(queryChoice);
        bOK = jdbcData.ExecuteQuery(e.GetQueryString(), params,
                e.GetAllLikeParams());
        return bOK;
    }

    /**
     * Execute update.
     *
     * @param queryChoice the query choice
     * @param parms the parms
     * @return true, if successful
     */
    public boolean ExecuteUpdate(int queryChoice, String [] parms) {
        boolean bOK;
        QueryData e = queryArray.get(queryChoice);
        bOK = jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        updateAmount = jdbcData.GetUpdateCount();
        return bOK;
    }

    /**
     * Connect to database.
     *
     * @param szHost the sz host
     * @param szUser the sz user
     * @param szPass the sz pass
     * @param szDatabase the sz database
     * @return true, if successful
     */
    public boolean Connect(String szHost, String szUser, String szPass,
                           String szDatabase) {
        boolean bConnect = jdbcData.ConnectToDatabase(szHost, szUser, szPass,
                szDatabase);
        if (!bConnect)
            error = jdbcData.GetError();
        return bConnect;
    }

    /**
     * Disconnect from database.
     *
     * @return true, if successful
     */
    public boolean Disconnect() {
        // Disconnect the JDBCData Object
        boolean bConnect = jdbcData.CloseDatabase();
        if (!bConnect)
            error = jdbcData.GetError();
        return true;
    }

    /**
     * Gets errors.
     *
     * @return the string
     */
    public String GetError() {
        return error;
    }
 
    private final QueryJDBC jdbcData;               // JDBC data
    private String error;                           // Errors.
    private final String projectTeamApplication;    // Title of app.
    private final ArrayList<QueryData> queryArray;  // Array of queries
    private int updateAmount;                       // Number of lines updated.
            
    /**
     * Runs console or GUI version of app, depending on argument provided.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0) {
            java.awt.EventQueue.invokeLater(
                    () -> new QueryFrame(queryrunner).setVisible(true));
        } else {
            if (args[0].equals ("-console")) {

                // Create Scanner object.
                Scanner keyboard = new Scanner(System.in);
                
                // Create boolean for connection status
                boolean bOK = true;

                // Connect()
                bOK = queryrunner.Connect(
                        "database-1.crvrlpwsgqaw.us-east-1.rds.amazonaws.com",
                        "admin", "group3aws", "Group3");

                System.out.println("\nADTRACKER\n\nEach query will be " +
                        "printed, followed by its results. If the\nquery " +
                        "requires parameters, the user will be prompted for them.");

                // n = GetTotalQueries()
                int n = queryrunner.GetTotalQueries();

                // Add empty line.
                System.out.println();

                // Iterate through queries.
                for (int i = 0; i < n; i++) {

                    // Initialize a parameter array to null.
                    String[] paramArray = {};

                    // Print query.
                    System.out.println(queryrunner.GetQueryText(i));

                    // Check if query has parameters.
                    if (queryrunner.isParameterQuery(i)) {
                        // amt = find out how many parameters it has
                        int amt = queryrunner.GetParameterAmtForQuery(i);

                        // Create a parameter array of strings for that amount
                        paramArray = new String[amt];
                        System.out.println();
                        for (int j = 0; j < amt; j++) {
                            // Get the parameter label for query and print it to
                            // console. Ask the user to enter a value
                            System.out.print(queryrunner.GetParamText(i, j) + ": ");
                            // Take the value and put it into parameter array
                            paramArray[j] = keyboard.nextLine();
                        }
                    }
                    // If it is an action query then
                    if (queryrunner.isActionQuery(i)) {
                        // call ExecuteUpdate to run the Query
                        queryrunner.ExecuteUpdate(i, paramArray);

                        // call GetUpdateAmount to find out how many rows
                        // were affected, and print that value
                        System.out.println(queryrunner.GetUpdateAmount() +
                                " rows affected.\n");

                    } else {
                        // call ExecuteQuery
                        queryrunner.ExecuteQuery(i, paramArray);

                        // call GetQueryData to get the results back
                        String[] headers = queryrunner.GetQueryHeaders();
                        String[][] data = queryrunner.GetQueryData();

                        // Print out all the results
                        System.out.println();
                        for (String header : headers) {
                            System.out.printf("%-32s", header);
                        }
                        System.out.println();

                        for (int r = 0; r < data.length; r++) {
                            for (int c = 0; c < data[0].length; c++) {
                                System.out.printf("%-32s", data[r][c]);
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }
                }
                // Close Scanner.
                keyboard.close();

                // Print errors.
                String errors = queryrunner.GetError();
                if (errors.isEmpty())
                    System.out.println("Completed with no errors");
                else
                    System.out.println("\nErrors: " + errors);

                // Disconnect()
                if (bOK = false) {
                    queryrunner.Disconnect();
                }
            }
        }
 
    }    
}
