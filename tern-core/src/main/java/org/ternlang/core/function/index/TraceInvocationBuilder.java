package org.ternlang.core.function.index;

import org.ternlang.core.function.ArgumentConverter;
import org.ternlang.core.function.Function;
import org.ternlang.core.function.Invocation;
import org.ternlang.core.function.Origin;
import org.ternlang.core.function.Signature;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.stack.ThreadStack;

public class TraceInvocationBuilder {
   
   private final ThreadStack stack;
   
   public TraceInvocationBuilder(ThreadStack stack) {
      this.stack = stack;
   }
   
   public Invocation create(Function function) {
      Signature signature = function.getSignature();
      Origin origin = signature.getOrigin();
      
      if(origin.isPlatform()) {
         return new PlatformInvocation(function, signature, stack);
      }
      if(origin.isSystem()) {
         return new SystemInvocation(function, signature);
      }
      return new DefaultInvocation(function, signature, stack);
   }
   
   private static class DefaultInvocation implements Invocation {
      
      private final Signature signature;
      private final ThreadStack stack;
      private final Function function;
      
      public DefaultInvocation(Function function, Signature signature, ThreadStack stack) {
         this.signature = signature;
         this.function = function;
         this.stack = stack;
      }
      
      @Override
      public Object invoke(Scope scope, Object object, Object... arguments) throws Exception{
         Invocation invocation = function.getInvocation();
         ArgumentConverter converter = signature.getConverter();
         Object[] list = converter.assign(arguments);
            
         try {
            stack.before(function);
            return invocation.invoke(scope, object, list);
         } finally {
            stack.after(function);
         }
      }
   }
   
   private static class PlatformInvocation implements Invocation {
      
      private final Signature signature;
      private final ThreadStack stack;
      private final Function function;
      
      public PlatformInvocation(Function function, Signature signature, ThreadStack stack) {
         this.signature = signature;
         this.function = function;
         this.stack = stack;
      }
      
      @Override
      public Object invoke(Scope scope, Object object, Object... arguments) throws Exception{
         Invocation invocation = function.getInvocation();
         ArgumentConverter converter = signature.getConverter();
         Object[] list = converter.convert(arguments);
            
         try {
            stack.before(function);
            return invocation.invoke(scope, object, list);
         } finally {
            stack.after(function);
         }
      }
   }
   
   private static class SystemInvocation implements Invocation {
   
      private final Signature signature;
      private final Function function;
      
      public SystemInvocation(Function function, Signature signature) {
         this.signature = signature;
         this.function = function;
      }
      
      @Override
      public Object invoke(Scope scope, Object object, Object... arguments) throws Exception{
         Invocation invocation = function.getInvocation();
         ArgumentConverter converter = signature.getConverter();
         Object[] list = converter.assign(arguments);
            
         return invocation.invoke(scope, object, list);
      }
   }
}