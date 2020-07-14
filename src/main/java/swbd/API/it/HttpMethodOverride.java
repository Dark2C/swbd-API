package swbd.API.it;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;

@Provider
@PreMatching
public class HttpMethodOverride implements ContainerRequestFilter {
	public void filter(ContainerRequestContext ctx) throws IOException {
		String methodOverride = ctx.getHeaderString("X-Http-Method-Override");
		if (methodOverride != null)
			ctx.setMethod(methodOverride);
	}
}
