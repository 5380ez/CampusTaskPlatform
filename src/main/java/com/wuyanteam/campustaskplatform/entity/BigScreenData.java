package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class BigScreenData {
   private long userNum;
   private int []oneWeekUserNum = new int[7];
   private int []campusNum = new int[4];
   private int taskNum;
   private int []untakenNum = new int[7];
   private int []incompleteNum = new int[7];
   private int []completeNum = new int[7];
   private int []timeoutNum = new int[7];
   private int []uncomfirmedNum = new int[7];
   private int likeNum;
   private int commentNum;
   private int []dailyActiveUsers = new int[7];
   private float []dailyActiveRates = new float[7];
   private float maleRate;
   private float femaleRate;
   private float oneWeekActiveRate;
}
