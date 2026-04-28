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
public final class DiscountViewModel_Factory implements Factory<DiscountViewModel> {
  @Override
  public DiscountViewModel get() {
    return newInstance();
  }

  public static DiscountViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DiscountViewModel newInstance() {
    return new DiscountViewModel();
  }

  private static final class InstanceHolder {
    private static final DiscountViewModel_Factory INSTANCE = new DiscountViewModel_Factory();
  }
}
