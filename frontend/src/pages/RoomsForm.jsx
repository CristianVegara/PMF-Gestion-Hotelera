import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

const token = localStorage.getItem('user_token');

export default function RoomsForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const isEdit = Boolean(id);
  
  const [form, setForm] = useState({
    number: "",
    type: "",
    price: "",
    available: true,
  });
  
  const [errors, setErrors] = useState([]);
  
  useEffect(() => {
    if (!isEdit) return;
    
    fetch(`/rooms/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then((res) => res.json())
    .then((data) => {
      setForm({
        number: data.number ?? "",
        type: data.type ?? "",
        price: data.price ?? "",
        available: data.available ?? false,
      });
    });
  }, [id]);
  
  const handleChange = (e) => {
    const { name, type, value, checked } = e.target;
    
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };
  
  const validate = () => {
    const errs = [];
    
    if (!form.number) errs.push("Error 1");
    if (!form.type) errs.push("Error 2");
    
    setErrors(errs);
    return errs.length === 0;
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validate()) return;
    
    const url = isEdit ? `/rooms/${id}` : "/rooms";
    const method = isEdit ? "PUT" : "POST";
    
    await fetch(url, {
      method,
      headers: { 
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(form),
    });
    
    window.alert(isEdit ? "Actualizado" : "Creado");
    navigate("/rooms");
  };
  
  return (
    <div className="form-container">
    <h2>{isEdit ? "Editar Habitación" : "Nueva Habitación"}</h2>
    
    <form onSubmit={handleSubmit}>
    <div className="form-group">
    <label htmlFor="number">Número</label>
    <input
    id="number"
    name="number"
    type="number"
    value={form.number}
    onChange={handleChange}
    />
    </div>
    
    <div className="form-group">
    <label htmlFor="type">Tipo</label>
    <input
    id="type"
    name="type"
    type="text"
    value={form.type}
    onChange={handleChange}
    />
    </div>
    
    <div className="form-group">
    <label htmlFor="price">Precio</label>
    <input
    id="price"
    name="price"
    type="number"
    step="0.01"
    value={form.price}
    onChange={handleChange}
    />
    </div>
    
    <div className="form-group">
    <label htmlFor="available">
    <input
    id="available"
    name="available"
    type="checkbox"
    checked={form.available}
    onChange={handleChange}
    />
    Disponible
    </label>
    </div>
    
    <div className="button-group">
    <button type="submit">
    {isEdit ? "Actualizar" : "Guardar"}
    </button>
    </div>
    </form>
    
    <div>
    {errors.map((e, i) => (
      <p key={i}>{e}</p>
    ))}
    </div>
    </div>
  );
}