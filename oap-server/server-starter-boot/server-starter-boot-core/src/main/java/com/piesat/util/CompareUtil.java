package com.piesat.util;

import com.piesat.enums.MonitorConditionEnum;
import com.piesat.skywalking.dto.ConditionDto;

import java.util.ArrayList;
import java.util.List;

public class CompareUtil {
  public static boolean compare(List<ConditionDto> conditionDtos,float f){

      List<Boolean> list=new ArrayList<>();
      for(ConditionDto conditionDto:conditionDtos){
          MonitorConditionEnum menum=MonitorConditionEnum.match(conditionDto.getParamname());
          if(MonitorConditionEnum.lte==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f<=value);
              list.add(flag);
          }
          if(MonitorConditionEnum.lt==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f<value);
              list.add(flag);
          }
          if(MonitorConditionEnum.gte==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f>=value);
              list.add(flag);
          }
          if(MonitorConditionEnum.gt==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f>value);
              list.add(flag);
          }
          if(MonitorConditionEnum.eq==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f==value);
              list.add(flag);
          }
          if(MonitorConditionEnum.noteq==menum){
              float value=Float.parseFloat(conditionDto.getParamvalue());
              boolean flag=(f!=value);
              list.add(flag);
          }
          if(MonitorConditionEnum.none==menum){
              boolean flag=(-1==f);
              list.add(flag);
          }
      }
      boolean flag=list.get(0);
      for(int i=1;i<list.size();i++){
         if("and".equals(conditionDtos.get(i).getOperate())){
             flag=(list.get(i)&&flag);
         }
         if("or".equals(conditionDtos.get(i).getOperate())){
             flag=(list.get(i)||flag);
         }
      }
      return flag;
  }

  public static void main(String[] args){
      List<ConditionDto> conditionDtos=new ArrayList<>();
      ConditionDto conditionDto=new ConditionDto();
      conditionDto.setParamname("gte");
      conditionDto.setParamvalue("50");
      ConditionDto conditionDto1=new ConditionDto();
      conditionDto1.setParamname("lte");
      conditionDto1.setOperate("and");
      conditionDto1.setParamvalue("60");
      conditionDtos.add(conditionDto);
      //conditionDtos.add(conditionDto1);
      boolean flag=compare(conditionDtos,76);
      System.out.println(flag);
  }
}
