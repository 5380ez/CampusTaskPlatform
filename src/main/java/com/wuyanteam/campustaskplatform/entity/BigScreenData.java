package com.wuyanteam.campustaskplatform.entity;

import lombok.Data;

@Data
public class BigScreenData {
   private long userNum;
   private int todayUserNum;
   private int []campusNum = new int[4];
   private int taskNum;
   private int untakenNum ;
   private int incompleteNum ;
   private int completeNum ;
   private int timeoutNum ;
   private int uncomfirmedNum;
   private int likeNum;
   private int commentNum;
   private int todayRegisterNum;
   private int []dailyActiveUsers = new int[7];
   private float []dailyActiveRates = new float[7];
   private float maleRate;
   private float femaleRate;
   private float oneWeekActiveRate;
}
