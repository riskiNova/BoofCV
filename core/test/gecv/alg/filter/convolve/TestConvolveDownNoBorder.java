/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.convolve;

import gecv.alg.filter.convolve.down.CompareToStandardConvolveDownNoBorder;
import org.junit.Test;


/**
 * @author Peter Abeles
 */
public class TestConvolveDownNoBorder {
	@Test
	public void compareToStandard() {
		CompareToStandardConvolveDownNoBorder test = new CompareToStandardConvolveDownNoBorder(ConvolveDownNoBorder.class);
		test.setSkip(2);

		for( int i = 0; i < 2; i++ ) {
			test.setImageDimention(15+i,20+i);
			// convolve with different kernel sizes relative to the skip amount
			test.setKernelRadius(1);
			test.performTests(15);
			test.setKernelRadius(2);
			test.performTests(15);
			test.setKernelRadius(3);
			test.performTests(15);
		}
	}
}