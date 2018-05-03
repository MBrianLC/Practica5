package es.ucm.fdi.view;

enum Command {
	Load("Load Events"), Save("Save Events"), SaveReport("Save Report"), GenReport("Generate"),
	ClearReport("ClearReport"), CheckIn("Insert"), Run("Run"), Reset("Reset"), Output("Redirect Output"),
	Clear("Clear"), Quit("Exit");
	
	private String text;
	
	Command(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
