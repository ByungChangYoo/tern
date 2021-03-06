package org.ternlang.core.variable;

import java.util.List;

import org.ternlang.core.convert.proxy.ProxyWrapper;

public class ListValue extends Value {
   
   private final ProxyWrapper wrapper;
   private final Integer index;
   private final List list;
   
   public ListValue(ProxyWrapper wrapper, List list, int index) {
      this.wrapper = wrapper;
      this.index = index;
      this.list = list;
   }
   
   @Override
   public Class getType() {
      Object value = list.get(index);
      Object object = wrapper.fromProxy(value);

      if(object != null) {
         return object.getClass();
      }
      return Object.class;
   }
   
   @Override
   public Object getValue(){
      Object value = list.get(index);
      
      if(value != null) {
         return wrapper.fromProxy(value);
      }
      return value;
   }
   
   @Override
   public void setValue(Object value){
      Object proxy = wrapper.toProxy(value);
      int length = list.size();
      
      for(int i = length; i <= index; i++) {
         list.add(null);
      }
      list.set(index, proxy);
   }       
   
   @Override
   public String toString() {
      return String.valueOf(list);
   }
}