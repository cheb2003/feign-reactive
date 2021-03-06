/**
 * Copyright 2018 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package reactivefeign.cloud;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import reactivefeign.ReactiveFeign;
import reactivefeign.testcase.IcecreamServiceApi;
import reactivefeign.webclient.WebReactiveFeign;

import java.util.function.Predicate;

/**
 * @author Sergii Karpenko
 */
public class RequestInterceptorTest extends reactivefeign.RequestInterceptorTest {

  @Override
  protected ReactiveFeign.Builder<IcecreamServiceApi> builder() {
    return CloudReactiveFeign.<IcecreamServiceApi>builder().setHystrixCommandSetterFactory(
            (target, methodMetadata) -> {
              String groupKey = target.name();
              HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(methodMetadata.configKey());
              return HystrixObservableCommand.Setter
                      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                      .andCommandKey(commandKey)
                      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                              .withExecutionTimeoutEnabled(false)
                      );
            }
    );
  }

  @Override
  protected Predicate<Throwable> notAuthorizedException() {
    return throwable -> throwable instanceof HystrixRuntimeException
            && throwable.getCause() instanceof FeignException;
  }
}
