/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.model.bpmn.impl.instance.zeebe;

import io.zeebe.model.bpmn.impl.BpmnModelConstants;
import io.zeebe.model.bpmn.impl.ZeebeConstants;
import io.zeebe.model.bpmn.impl.instance.BpmnModelElementInstanceImpl;
import io.zeebe.model.bpmn.instance.zeebe.ZeebeInput;
import io.zeebe.model.bpmn.instance.zeebe.ZeebeIoMapping;
import io.zeebe.model.bpmn.instance.zeebe.ZeebeOutput;
import io.zeebe.model.bpmn.instance.zeebe.ZeebeOutputBehavior;
import java.util.Collection;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.camunda.bpm.model.xml.type.child.ChildElementCollection;

public class ZeebeIoMappingImpl extends BpmnModelElementInstanceImpl implements ZeebeIoMapping {

  private static Attribute<ZeebeOutputBehavior> outputBehaviorAttribute;
  protected static ChildElementCollection<ZeebeInput> inputCollection;
  protected static ChildElementCollection<ZeebeOutput> outputCollection;

  public ZeebeIoMappingImpl(ModelTypeInstanceContext instanceContext) {
    super(instanceContext);
  }

  @Override
  public ZeebeOutputBehavior getOutputBehavior() {
    return outputBehaviorAttribute.getValue(this);
  }

  @Override
  public void setOutputBehavhior(ZeebeOutputBehavior behavior) {
    outputBehaviorAttribute.setValue(this, behavior);
  }

  @Override
  public Collection<ZeebeInput> getInputs() {
    return inputCollection.get(this);
  }

  @Override
  public Collection<ZeebeOutput> getOutputs() {
    return outputCollection.get(this);
  }

  public static void registerType(ModelBuilder modelBuilder) {
    final ModelElementTypeBuilder typeBuilder =
        modelBuilder
            .defineType(ZeebeIoMapping.class, ZeebeConstants.ELEMENT_IO_MAPPING)
            .namespaceUri(BpmnModelConstants.ZEEBE_NS)
            .instanceProvider(ZeebeIoMappingImpl::new);

    outputBehaviorAttribute =
        typeBuilder
            .enumAttribute(ZeebeConstants.ATTRIBUTE_OUTPUT_BEHAVIOR, ZeebeOutputBehavior.class)
            .namespace(BpmnModelConstants.ZEEBE_NS)
            .build();

    inputCollection = typeBuilder.sequence().elementCollection(ZeebeInput.class).build();
    outputCollection = typeBuilder.sequence().elementCollection(ZeebeOutput.class).build();

    typeBuilder.build();
  }
}
