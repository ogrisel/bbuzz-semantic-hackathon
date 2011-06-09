package corpusrefiner;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.parser.JSONParser;

public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 4048990005904721961L;

	private static final JSONParser jsonParser = new JSONParser();

	@Override
	public void init() throws ServletException {

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String serializedDoc = request.getParameter("doc");
		System.out.println(serializedDoc);
	}
}
