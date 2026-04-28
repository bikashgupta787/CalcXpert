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
public final class LoanViewModel_Factory implements Factory<LoanViewModel> {
  @Override
  public LoanViewModel get() {
    return newInstance();
  }

  public static LoanViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LoanViewModel newInstance() {
    return new LoanViewModel();
  }

  private static final class InstanceHolder {
    private static final LoanViewModel_Factory INSTANCE = new LoanViewModel_Factory();
  }
}
