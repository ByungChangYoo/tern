package tern.tree.define;

import static tern.core.constraint.Constraint.NONE;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import tern.core.constraint.Constraint;
import tern.core.module.Module;
import tern.core.scope.Scope;
import tern.core.scope.ScopeState;
import tern.core.type.Type;
import tern.core.type.TypeBody;
import tern.core.variable.Value;
import tern.tree.annotation.AnnotationList;
import tern.tree.constraint.ClassName;

public class ClassBuilder {   
   
   private final AtomicReference<Type> reference;
   private final ClassPropertyGenerator generator;
   private final ConstantPropertyBuilder builder;
   private final AnnotationList annotations;
   private final TypeHierarchy hierarchy;
   private final ClassName name;
   
   public ClassBuilder(AnnotationList annotations, ClassName name, TypeHierarchy hierarchy) {
      this.reference = new AtomicReference<Type>();
      this.generator = new ClassPropertyGenerator();
      this.builder = new ConstantPropertyBuilder();
      this.annotations = annotations;
      this.hierarchy = hierarchy;
      this.name = name;
   }
   
   public Type create(TypeBody body, Scope outer) throws Exception {
      Module module = outer.getModule();
      String alias = name.getName(outer);
      int modifiers = name.getModifiers(outer);
      Type enclosing = outer.getType();
      Type type = module.addType(alias, modifiers); 

      if(enclosing != null) {
         String name = type.getName();
         String prefix = enclosing.getName();
         String key = name.replace(prefix + '$', ""); // get the class name
         Value value = Value.getConstant(type);
         ScopeState state = outer.getState();
         
         builder.createStaticProperty(body, key, enclosing, NONE);
         state.addValue(key, value);
      } 
      reference.set(type);
      
      return type;
   }
   
   public Type define(TypeBody body, Scope outer) throws Exception {
      Type type = reference.get();
      Scope scope = type.getScope();
      List<Constraint> generics = name.getGenerics(scope);
      List<Constraint> constraints = type.getGenerics();      
     
      constraints.addAll(generics);
      annotations.apply(scope, type);
      generator.generate(body, scope, type);
      hierarchy.define(scope, type); 
      
      return type;
   }
   
   public Type compile(TypeBody body, Scope outer) throws Exception {
      Type type = reference.get();
      
      hierarchy.compile(outer, type);
      
      return type;
   }
}