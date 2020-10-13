#version 330 core
in vec4 gl_FragCoord;
out vec4 out_color;

void main() {
    out_color = vec4(gl_FragCoord.z, gl_FragCoord.x / 900, gl_FragCoord.y / 700, 1.0);
}