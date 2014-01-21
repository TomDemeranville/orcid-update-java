package uk.bl.odin.orcid.domain;

/**
 * General Bibtex utilities
 */
public class BibtexBuilder {

	private static final BibtexBuilder instance = new BibtexBuilder();

	private BibtexBuilder() {
	}

	/**
	 * Constructs a Bibtext PHD citation from its component parts. Escapes
	 * characters, generates an ID
	 * 
	 * @return a bibtex encoded string
	 */
	public String buildPHDCitation(String author, String title, String institution, String year) {
		StringBuilder b = new StringBuilder();
		b.append("@PhDThesis{" + generateBibIdentifier(author, year, title) + ",\n");
		b.append("author = {" + escapeLatex(author) + "},\n");
		b.append("title = {" + escapeLatex(title) + "},\n");
		b.append("school = {" + escapeLatex(institution) + "},\n");
		b.append("year = " + escapeLatex(year) + "\n");
		b.append("}");
		return b.toString();
	}

	/**
	 * Creates a bibtex identifier from some parts of the metadata With thanks
	 * to https://github.com/uschindler
	 * 
	 */
	public String generateBibIdentifier(String author, String year, String title) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = author.length(); i < c; i++) {
			char ch = Character.toLowerCase(author.charAt(i));
			if (ch >= 'a' && ch <= 'z')
				sb.append(ch);
		}
		sb.append(year.trim());
		int j = 0;
		boolean start = true;
		for (int i = 0, c = title.length(); i < c; i++) {
			char ch = Character.toLowerCase(title.charAt(i));
			start |= (ch == ' ');
			if (start && ch >= 'a' && ch <= 'z') {
				sb.append(ch);
				j++;
				start = false;
			}
			if (j >= 4)
				break;
		}
		return sb.toString();
	}

	/**
	 * Escapes accents and special characters, bibtex style With thanks to
	 * https://github.com/uschindler
	 * 
	 */
	public String escapeLatex(String text) {
		StringBuilder sb = new StringBuilder(text.length());
		boolean nl = false;
		for (int i = 0, c = text.length(); i < c; i++) {
			char ch = text.charAt(i);
			if (ch != 13 && ch != 10 && nl) {
				sb.append("\\\\\n");
				nl = false;
			}
			switch (ch) {
			case '\u00E4':
				sb.append("{\\\"a}");
				break;
			case '\u00F6':
				sb.append("{\\\"o}");
				break;
			case '\u00FC':
				sb.append("{\\\"u}");
				break;
			case '\u00EB':
				sb.append("{\\\"e}");
				break;
			case '\u00EF':
				sb.append("{\\\"i}");
				break;

			case 196:
				sb.append("{\\\"A}");
				break;
			case 214:
				sb.append("{\\\"O}");
				break;
			case 220:
				sb.append("{\\\"U}");
				break;
			case 203:
				sb.append("{\\\"E}");
				break;
			case 207:
				sb.append("{\\\"I}");
				break;

			case 225:
				sb.append("{\\'a}");
				break;
			case 243:
				sb.append("{\\'o}");
				break;
			case 250:
				sb.append("{\\'u}");
				break;
			case 233:
				sb.append("{\\'e}");
				break;
			case 237:
				sb.append("{\\'i}");
				break;

			case 224:
				sb.append("{\\`a}");
				break;
			case 242:
				sb.append("{\\`o}");
				break;
			case 249:
				sb.append("{\\`u}");
				break;
			case 232:
				sb.append("{\\`e}");
				break;
			case 236:
				sb.append("{\\`i}");
				break;

			case 226:
				sb.append("{\\^a}");
				break;
			case 244:
				sb.append("{\\^o}");
				break;
			case 251:
				sb.append("{\\^u}");
				break;
			case 234:
				sb.append("{\\^e}");
				break;
			case 238:
				sb.append("{\\^i}");
				break;

			case 194:
				sb.append("{\\^A}");
				break;
			case 212:
				sb.append("{\\^O}");
				break;
			case 219:
				sb.append("{\\^U}");
				break;
			case 202:
				sb.append("{\\^E}");
				break;
			case 206:
				sb.append("{\\^I}");
				break;

			case 227:
				sb.append("{\\~a}");
				break;
			case 241:
				sb.append("{\\~n}");
				break;
			case 245:
				sb.append("{\\~o}");
				break;

			case 195:
				sb.append("{\\~A}");
				break;
			case 209:
				sb.append("{\\~N}");
				break;
			case 213:
				sb.append("{\\~O}");
				break;

			case '\u00DF':
				sb.append("{\\ss}");
				break;
			case '\u00A0':
				sb.append('~');
				break; // &nbsp;
			case '\u00BA':
				sb.append("{\\textdegree}");
				break;
			case '"':
				sb.append("{\"}");
				break;

			case 13:
			case 10:
				nl = true;
				break;

			case '\'':
			case '\u00B4':
			case '`':
				sb.append("{\'}");
				break;

			// simple escapes:
			case '\\':
			case '~':
			case '$':
			case '%':
			case '^':
			case '&':
			case '{':
			case '}':
			case '_':
				sb.append('\\');
				sb.append(ch);
				break;
			default:
				sb.append((ch < 0x80) ? ch : '?');
			}
		}
		return sb.toString();
	}

	public static BibtexBuilder getInstance() {
		return instance;
	}

}
