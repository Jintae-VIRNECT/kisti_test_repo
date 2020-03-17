export function Member(json) {
  return {
    id: json.uuid,
    email: json.email,
    name: json.name,
    image: json.profile,
    role: json.role,
  }
}
