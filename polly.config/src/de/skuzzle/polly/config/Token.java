package de.skuzzle.polly.config;


class Token {

	private Span position;
	private String stringValue;
	private TokenType tokenType;
	
	
	
	public Token(String value, TokenType tokenType, Span position) {
		this.stringValue = value;
		this.tokenType = tokenType;
		this.position = position;
	}
	

	
	public Token(TokenType tokenType, Span position) {
	    this(tokenType.toString(), tokenType, position);
	}
	
	
	
	public boolean matches(TokenType type) {
		return this.tokenType == type;
	}
	
	
	
	public Span getPosition() {
		return this.position;
	}
	
	
	
	public TokenType getTokenType() {
		return this.tokenType;
	}
	
	
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	
	
	@Override
	public String toString() {
	    StringBuilder result = new StringBuilder();
	    result.append(this.tokenType.toString());
	    switch (this.tokenType) {
	    case STRING:
	    case IDENTIFIER:
	    case BLOCKCOMMENT:
	    case INLINECOMMENT:
	        result.append("(\"");
	        result.append(this.stringValue);
	        result.append("\")");
	    }
	    result.append(" ");
	    result.append(this.position.toString());
	    return result.toString();
	}
}