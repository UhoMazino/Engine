#version 330 core
in vec4 gl_FragCoord;
in vec2 texcord;
out vec4 out_color;
uniform sampler2D tex;

void main() {
    out_color = texture(tex, texcord);
    //    out_color = vec4(gl_FragCoord.z, gl_FragCoord.x / 800, gl_FragCoord.y / 600, 1.0);
}