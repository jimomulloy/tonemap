package comul01.tonemap;


/**
 *  Wavelet analysis class
 */

public final class Wavelet {

	static final double pi = 3.14159265;

	public static final void convert(	double[] wave,
					double[] s,
	   	 			double[] freq,
	   	 			double dt,   
	   				double pFactor,
	   				double tFactor,
	   				int	skip,
	   				int mm,
	   				int	nn,
	   				ProgressListener progressListener
	  	   		) {
	
		int i, j, index=0, ii=0, lmin=0, lmax=0, offset=0, offsetmin=0, offsetmax=0, step=1;
		double tval, t, k, nan, t1, t2;
		double[] period;
		double[] kvals;
		double ps, ctotr, ctoti;

		period = new double[nn];
		kvals = new double[nn];
		nan=0; 

		for (j=0; j<nn; j++) {
			period[j]=1.0/freq[j];
			kvals[j]=freq[j];
		}         
				
		for (i=0; i<wave.length; i++) {
			wave[i]=nan;
		}
		
		System.out.println("step   i    kvals     freq     period        offset  pFactor   mm   nn");

		for (i=0; i<nn; i++) {
		
			progressListener.setProgress((int)(((double)(index+1)/(double)wave.length)*100.0));
			offset=(int)Math.floor(((pFactor+1.0)/2.0)*period[i]/dt);
			offsetmax=(int)Math.floor(((pFactor+1.0)/2.0)*period[0]/dt);
			offsetmin=(int)Math.floor(((pFactor+1.0)/2.0)*period[nn-1]/dt);
			//step = 1 + (int)Math.floor((10*(double)(offset-offsetmin)/(double)(offsetmax-offsetmin)));
			k=kvals[i];
			step = Math.max((int)(1/(k*dt*tFactor)), 1);
				
			System.out.println(step + " "+i+" "+kvals[i]+
				" "+freq[i]+" "+period[i]+" "+offset+" "+pFactor+" "+" "+mm+" "+nn);
			if (offset*2 < mm ) {
			//	for (j=offset; j<(mm-offset); j++) {
				for (j=0; j<mm; j++) {
			
					//	System.out.println("offset j "+offset+j);
					index=(i*mm+j)/skip;
					if (j % skip == 0) {
						
						t=j*dt;
						//lmin=Math.max(j-(int)Math.floor(1.5*offset),0);
						//lmax=Math.min(j+(int)Math.ceil(1.5*offset),(mm-1));
						lmin=j-(int)Math.floor(1.5*offset);
						lmax=j+(int)Math.ceil(1.5*offset);
						ctotr=0;
						ctoti=0;
						//	System.out.println("limn lmax offset mm j "
						//			+ lmin + " " + lmax + " " + offset + " " + mm + " " + j);
						// account for resolution here !!
							
						for (ii=lmin; ii<=lmax; ii+=step) {
			/* integrate s*conj(psi) - note s is always real. We can
			 speed things up a bit if we calculate the terms used in
			 the real and imaginary parts of ctot in one go... not
			 very neat though*/
			 				tval = ii*dt;
			 				if (ii>=0 && ii<mm) {
								t1=s[ii]*Math.exp(-2.0*k*(tval-t)*k*(tval-t)*pi*pi/(pFactor*pFactor));
								t2=Math.exp((-pFactor*pFactor / 2.0) - 2*k*(tval-t)*k*(tval-t)*pi*pi/(pFactor*pFactor));
								ctotr=ctotr+Math.sin(2.0* pi*k*(tval-t))*t1 - t2;
								ctoti=ctoti+Math.cos(2.0* pi*k*(tval-t))*t1 - t2;
			 				} else {
				 				t1 = 0;
				 				t2=Math.exp((-pFactor*pFactor / 2.0) - 2*k*(tval-t)*k*(tval-t)*pi*pi/(pFactor*pFactor));
				 				ctotr=ctotr-t2;
								ctoti=ctoti-t2;
			 				}		 				
				 		}
												
						wave[index]=Math.sqrt(k)*Math.sqrt(ctotr*ctotr+ctoti*ctoti);
						//	System.out.println("step j wave index offset lmin lmax" + step + " " 
						//		+j+" " + wave[index]+" "+index+" " +offset+ " " + lmin + " " + lmax);
							/* (1+lmax-lmin); hmmmm...*/
					}
				
				}

			}
		}
	progressListener.setProgress(100);
	return ;
	}

}