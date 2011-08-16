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

package gecv.abst.filter;

import gecv.abst.filter.convolve.FactoryConvolve;
import gecv.alg.filter.convolve.ConvolveImageNoBorder;
import gecv.alg.filter.kernel.FactoryKernel;
import gecv.alg.misc.ImageTestingOps;
import gecv.core.image.border.BorderType;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.image.ImageFloat32;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public class TestFilterSequence {

	Random rand = new Random(234);
	int radius = 2;
	int width = 30;
	int height = 40;

	/**
	 * Perform a sequence of convolutions manually.  Should produce the same results
	 */
	@Test
	public void compareToManualSequence() {
		Kernel1D_F32 ker1 = FactoryKernel.random1D_F32(radius,0,5,rand);
		Kernel1D_F32 ker2 = FactoryKernel.random1D_F32(radius+1,0,5,rand);
		Kernel1D_F32 ker3 = FactoryKernel.random1D_F32(radius+2,0,5,rand);

		ImageFloat32 input = new ImageFloat32(width,height);
		ImageTestingOps.randomize(input,rand,0,10);
		ImageFloat32 found = new ImageFloat32(width,height);
		ImageFloat32 expected = new ImageFloat32(width,height);

		FilterImageInterface f1 = FactoryConvolve.convolve(ker1,ImageFloat32.class,ImageFloat32.class, BorderType.SKIP, true);
		FilterImageInterface f2 = FactoryConvolve.convolve(ker2,ImageFloat32.class,ImageFloat32.class, BorderType.SKIP, true);
		FilterImageInterface f3 = FactoryConvolve.convolve(ker3,ImageFloat32.class,ImageFloat32.class, BorderType.SKIP, true);

		FilterSequence sequence = new FilterSequence(f1,f2,f3);
		sequence.process(input,found);
		assertEquals(radius+2,sequence.borderHorizontal);
		assertEquals(radius+2,sequence.borderVertical);

		ImageFloat32 tmp1 = new ImageFloat32(width,height);
		ImageFloat32 tmp2 = new ImageFloat32(width,height);
		ConvolveImageNoBorder.horizontal(ker1,input,tmp1,false);
		ConvolveImageNoBorder.horizontal(ker2,tmp1,tmp2,false);
		ConvolveImageNoBorder.horizontal(ker3,tmp2,expected,false);

		GecvTesting.assertEquals(expected,found,0,1e-4f);
	}
}