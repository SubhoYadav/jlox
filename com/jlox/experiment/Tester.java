package com.jlox.experiment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Tester {
    private static int recursion(int i, int j, int m, int n, int[][] dp) {
        if (i == m-1 && j == n-1) {
            return 1;
        }
        else if (i < 0 || j < 0 || i > m-1 || j > n-1) {
            return 0;
        }
        if (dp[i][j] != -1) return dp[i][j];

        int right = recursion(i, j + 1, m, n, dp);
        int bottom = recursion(i+1, j, m, n, dp);

        return dp[i][j] = right + bottom;
    }

    private static int tabulation (int m, int n) {
        int dp[][] = new int[m][n];
        for(int i=0; i<n; i++)
            dp[0][i] = 1;

        int row = 0, col = 0, up, left;
        for (int i=1; i<m; i++) {
            for (int j=0; j<n; j++) {
                // going up
                row = i-1; col = j;
                if (row < 0 || row > m-1 || col < 0 || col > n-1)
                    up = 0;
                else
                    up = dp[row][col];

                // going left
                row = i; col = j - 1;
                if (row < 0 || row > m-1 || col < 0 || col > n-1)
                    left = 0;
                else
                    left = dp[row][col];

                dp[i][j] = left + up;
            }
        }

        for (int i=0; i<m; i++) {
            for (int j=0; j<n; j++) {
                System.out.print(dp[i][j] + " ");
            }
            
            System.out.println();
        }
        return 0;
    }

    private static int minPathSum (int i, int j, int[][] grid, int m, int n) {
        if (i == m-1 && j == n-1) {
            return grid[i][j];
        }

        int bottom = Integer.MAX_VALUE;
        int right = Integer.MAX_VALUE;
        int row = -1, col = -1;

        // cost if I go bottom
        row = i + 1; col = j;
        if (row >= 0 && row <= m-1 && col >= 0 && col <= n-1) {
            bottom = grid[i][j] + minPathSum(row, col, grid, m, n);
        }
        
        // cost if I go right
        row = i; col = j+1;
        if (row >= 0 && row <= m-1 && col >= 0 && col <= n-1) {
            right = grid[i][j] + minPathSum(row, col, grid, m, n);
        }

        return Math.min(bottom, right);
    }

    private static int triangleMinPathSum (int[][] triangle, int row, int col) {
        if (row == triangle.length - 1) {
            return triangle[row][col];
        }
        
        int bottom = Integer.MAX_VALUE;
        // Moving bottom
        if (row + 1 < triangle.length) {
            bottom = triangle[row][col] + triangleMinPathSum(triangle, row + 1, col);
        }

        // Moving diagonal
        int diag = Integer.MAX_VALUE;
        if (row + 1 < triangle.length && col+1 < triangle[row+1].length) {
            diag = triangle[row][col] + triangleMinPathSum(triangle, row + 1, col + 1);
        }

        return Math.min(diag, bottom);
    }

    private static int triangleMinPathSumTab (int[][] triangle) {
        // the space optimized solution is implemented starting from the end, cause when we overwrite a particular cell, we are sure that its value will never be used again
        int n = triangle[triangle.length - 1].length;
        int dp[] = new int[n];
        for (int i=0; i<n; i++) dp[i] = triangle[triangle.length - 1][i];
    
        for (int i=triangle.length - 2; i>=0; i--) {
            for (int j=0; j<triangle[i].length; j++) {
                dp[j] = triangle[i][j] + Math.min(
                    dp[j],
                    dp[j+1]
                );
            }
        }

        return dp[0];
    }

    public static void main(String[] args) {
        int m = 3, n = 7;

        int[][] dp = new int[m][n];
        for (int i=0; i<m; i++)
            for (int j=0; j<n; j++)
                dp[i][j] = -1;

        // System.out.println(recursion(0, 0, m, n, dp));
        // System.out.println(tabulation(m, n));

        int[][] grid = {
            {1,3,1},
            {1,5,1},
            {4,2,1}
        };
        m = grid.length; n = grid[0].length;

        int ans = minPathSum(0,0,grid,m,n);
        // System.out.println(ans);

        int[][] triangle = {
            {1},
            {2,3},
            {3,6,7},
            {8,9,6,10}
        };

        // ans = triangleMinPathSum(triangle, 0, 0);
        // ans = triangleMinPathSumTab(triangle);
        // System.out.println(ans);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,2);
        map.put(3,4);
        map.put(5,6);

        System.out.println(map.size());
        map.remove(3);
        System.out.println(map.size());

    }
}