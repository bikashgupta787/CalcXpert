package com.smartcalc.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class BmiViewModel_Factory implements Factory<BmiViewModel> {
  @Override
  public BmiViewModel get() {
    return newInstance();
  }

  public static BmiViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BmiViewModel newInstance() {
    return new BmiViewModel();
  }

  private static final class InstanceHolder {
    private static final BmiViewModel_Factory INSTANCE = new BmiViewModel_Factory();
  }
}
