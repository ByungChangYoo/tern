package org.ternlang.tree;

import static org.ternlang.core.result.Result.NORMAL;

import org.ternlang.core.Execution;
import org.ternlang.core.Statement;
import org.ternlang.core.constraint.Constraint;
import org.ternlang.core.error.InternalStateException;
import org.ternlang.core.result.Result;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.index.ScopeIndex;
import org.ternlang.core.resume.Resume;
import org.ternlang.core.resume.Yield;

public class StatementBlock extends Statement {
   
   private volatile StatementCompiler compiler;
   private volatile StatementBuilder builder;
   
   public StatementBlock(Statement... statements) {
      this.builder = new StatementBuilder(statements);
   }

   @Override
   public void create(Scope scope) throws Exception {
      builder.create(scope);
   }
   
   @Override
   public boolean define(Scope scope) throws Exception {
      if(compiler == null) {
         compiler = builder.define(scope);
      }
      return true;
   }
   
   @Override
   public Execution compile(Scope scope, Constraint returns) throws Exception {
      if(compiler == null) {
         throw new InternalStateException("Statement was not created");
      }
      return compiler.compile(scope, returns);
   }
   
   private static class StatementBuilder {
      
      private final Statement[] statements;
      private final Statement[] executable;
      
      public StatementBuilder(Statement[] statements) {
         this.executable = new Statement[statements.length];
         this.statements = statements;
      }

      public void create(Scope scope) throws Exception {
         for(int i = 0; i < statements.length; i++){
            Statement statement = statements[i];

            if(statement != null) {
               statement.create(scope);
            }
         }
      }
      
      public StatementCompiler define(Scope scope) throws Exception {
         ScopeIndex index = scope.getIndex();
         int size = index.size();
         
         try {
            for(int i = 0; i < statements.length; i++){
               Statement statement = statements[i];
            
               if(statement.define(scope)) {
                  executable[i] = statement;
                  statements[i] = null;
               }
            }
         } finally {
            index.reset(size);
         }
         return new StatementCompiler(statements, executable);
      }
   }
   
   private static class StatementCompiler {
      
      private final Statement[] statements;
      private final Statement[] executable;      
      private final Execution[] executions;
      
      public StatementCompiler(Statement[] statements, Statement[] executable) {
         this.executions = new Execution[statements.length];
         this.executable = executable;
         this.statements = statements;
      }
      
      public Execution compile(Scope scope, Constraint returns) throws Exception {
         for(int i = 0; i < executable.length; i++) {
            Statement statement = executable[i];
            
            if(statement != null) {
               executions[i]  = statement.compile(scope, returns);
            }
         }
         for(int i = 0; i < statements.length; i++) {
            Statement statement = statements[i];
            
            if(statement != null) {
               executions[i]  = statement.compile(scope, returns);
            }
         }
         return new StatementExecutor(executions);
      }
   }
   
   private static class StatementExecutor extends SuspendStatement<Integer> {
      
      private final Execution[] executions;     
      private final Integer start;
      
      public StatementExecutor(Execution[] executions) {  
         this(executions, 0);
      }
      
      public StatementExecutor(Execution[] executions, int start) {  
         this.executions = executions;
         this.start = start;
      }      
      
      @Override
      public Result execute(Scope scope) throws Exception {
         return resume(scope, start);
      }
      
      @Override
      public Result resume(Scope scope, Integer index) throws Exception {
         Result last = NORMAL;

         for(int i = index; i < executions.length; i++){
            Execution execution = executions[i];
            Result result = execution.execute(scope);
            
            if(result.isYield()) {
               return suspend(scope, result, this, i + 1);
            }
            if(!result.isNormal()){
               return result;
            }
            last = result;
         }
         return last;
      }
      
      @Override
      public Resume suspend(Result result, Resume resume, Integer value) throws Exception {
         Yield yield = result.getValue();
         Resume child = yield.getResume();
         
         return new CompoundResume(child, resume, value);
      }
   }
}