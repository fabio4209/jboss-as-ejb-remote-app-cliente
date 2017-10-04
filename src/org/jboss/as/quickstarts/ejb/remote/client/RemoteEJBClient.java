package org.jboss.as.quickstarts.ejb.remote.client;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import org.jboss.as.quickstarts.ejb.remote.stateful.CounterBean;
import org.jboss.as.quickstarts.ejb.remote.stateful.RemoteCounter;
import org.jboss.as.quickstarts.ejb.remote.stateless.CalculatorBean;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;

/**
 * A sample program which acts a remote client for a EJB deployed on Wildfly
 * This program shows how to lookup stateful and stateless beans
 * via JNDI and then invoke on them 
 */
public class RemoteEJBClient {

	public static void main(String[] args) throws Exception {
		invokeStatelessBean();
		invokeStatefulBean();
	}

	private static void invokeStatelessBean() throws NamingException {
		// Let's lookup the remote stateless calculator
		final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();

		System.out.println("Obtained a remote stateless calculator for invocation");
		
		// invoke on the remote calculator
		int a = 204;
		int b = 340;
		System.out.println("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
		
		int sum = statelessRemoteCalculator.add(a, b);
		System.out.println("Remote calculator returned sum = " + sum);
		if (sum != a + b) {
			throw new RuntimeException("Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was " + (a + b));
		}
		
		// try one more invocation, this time for subtraction
		int num1 = 3434;
		int num2 = 2332;
		System.out.println("Subtracting " + num2 + " from " + num1 + " via the remote stateless calculator deployed on the server");
		int difference = statelessRemoteCalculator.subtract(num1, num2);
		System.out.println("Remote calculator returned difference = " + difference);
		if (difference != num1 - num2) {
			throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference + " ,expected difference was " + (num1 - num2));
		}
	}

	private static void invokeStatefulBean() throws NamingException {

		// Let's lookup the remote stateful counter
		final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulCounter();
		System.out.println("Obtained a remote stateful counter for invocation");

		// invoke on the remote counter bean
		final int NUM_TIMES = 20;
		System.out.println("Counter will now be incremented " + NUM_TIMES + " times");
		for (int i = 0; i < NUM_TIMES; i++) {
			System.out.println("Incrementing counter");
			statefulRemoteCounter.increment();
			System.out.println("Count after increment is " + statefulRemoteCounter.getCount());
		}

		// now decrementing
		System.out.println("Counter will now be decremented " + NUM_TIMES + " times");
		for (int i = NUM_TIMES; i > 0; i--) {
			System.out.println("Decrementing counter");
			statefulRemoteCounter.decrement();
			System.out.println("Count after decrement is " + statefulRemoteCounter.getCount());
		}

	}

	private static RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {

		final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		
		// The app name is the application name of the deployed EJBs. This is typically the ear name
		// without the .ear suffix. However, the application name could be overridden in the application.xml of the
		// EJB deployment on the server.
		// Since we haven't deployed the application as a .ear, the app name for us will be an empty string
		final String appName = "";
		
		// This is the module name of the deployed EJBs on the server. This is typically the jar name of the
		// EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
		// In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
		// jboss-as-ejb-remote-app
		final String moduleName = "jboss-as-ejb-remote-app";
		
		// AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
		// our EJB deployment, so this is an empty string
		final String distinctName = "";
		
		// The EJB name which by default is the simple class name of the bean implementation class
		final String beanName = CalculatorBean.class.getSimpleName();
		
		// the remote view fully qualified class name
		final String viewClassName = RemoteCalculator.class.getName();
		
		// let's do the lookup
		RemoteCalculator remoteCalculator = (RemoteCalculator) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName); 
		
		return remoteCalculator;
	}
	

	private static RemoteCounter lookupRemoteStatefulCounter() throws NamingException {
		
		final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);
		
		// The app name is the application name of the deployed EJBs. 
		// This is typically the ear name without the .ear suffix. 
		// However, the application name could be overridden in the application.xml of the
		// EJB deployment on the server.
		// Since we haven't deployed the application as a .ear, the app name for us will be an empty string
		final String appName = "";
		
		// This is the module name of the deployed EJBs on the server. 
		// This is typically the jar name of the EJB deployment, without the .jar suffix, 
		// but can be overridden via the ejb-jar.xml
		// In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
		// jboss-as-ejb-remote-app
		final String moduleName = "jboss-as-ejb-remote-app";
		
		// AS7 allows each deployment to have an (optional) distinct name. 
		// We haven't specified a distinct name for our EJB deployment, so this is an empty string
		final String distinctName = "";
		
		// The EJB name which by default is the simple class name of the bean implementation class
		final String beanName = CounterBean.class.getSimpleName();
		
		// the remote view fully qualified class name
		final String viewClassName = RemoteCounter.class.getName();
		
		// notice the ?stateful string as the last part of the jndi name for stateful bean lookup
		return (RemoteCounter) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName + "?stateful");
	
	}
	
}