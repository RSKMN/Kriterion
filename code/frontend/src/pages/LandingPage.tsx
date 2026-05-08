import { ArrowRight, BarChart3, ShieldCheck, Wallet } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-50 selection:bg-indigo-500/30">
      {/* Navigation */}
      <nav className="container mx-auto px-6 py-4 flex justify-between items-center relative z-10">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center font-bold text-xl shadow-lg shadow-indigo-500/20">K</div>
          <span className="text-xl font-semibold tracking-tight">Kriterion</span>
        </div>
        <div className="flex items-center gap-6">
          <Link to="/login" className="text-sm font-medium text-slate-300 hover:text-white transition-colors">
            Log in
          </Link>
          <Link
            to="/register"
            className="text-sm font-medium bg-white text-slate-900 px-4 py-2 rounded-full hover:bg-slate-200 transition-all duration-300 shadow-lg shadow-white/10 hover:shadow-white/20 active:scale-95"
          >
            Get Started
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <main className="container mx-auto px-6 pt-20 pb-32 text-center relative z-10">
        {/* Background glow effects */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-indigo-500/20 blur-[120px] rounded-full pointer-events-none" />
        <div className="absolute top-0 right-1/4 w-[400px] h-[400px] bg-purple-500/10 blur-[100px] rounded-full pointer-events-none" />

        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-slate-900/80 border border-slate-800 text-sm font-medium text-indigo-300 mb-8 backdrop-blur-sm animate-fade-in-up">
          <span className="w-2 h-2 rounded-full bg-indigo-500 animate-pulse" />
          Kriterion 1.0 is here
        </div>

        <h1 className="text-5xl md:text-7xl font-bold tracking-tight mb-8 leading-tight animate-fade-in-up" style={{ animationDelay: '100ms' }}>
          Master your finances with <br className="hidden md:block" />
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-cyan-400">
            intelligent precision
          </span>
        </h1>

        <p className="text-lg md:text-xl text-slate-400 mb-12 max-w-2xl mx-auto leading-relaxed animate-fade-in-up" style={{ animationDelay: '200ms' }}>
          Experience the next generation of transaction management. Advanced filtering, real-time analytics, and automated insights built for modern financial workflows.
        </p>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4 animate-fade-in-up" style={{ animationDelay: '300ms' }}>
          <Link
            to="/register"
            className="group flex items-center justify-center gap-2 w-full sm:w-auto bg-indigo-600 text-white px-8 py-4 rounded-full font-medium hover:bg-indigo-500 transition-all duration-300 shadow-lg shadow-indigo-500/25 hover:shadow-indigo-500/40 active:scale-95"
          >
            Start for free
            <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
          <a
            href="#features"
            className="flex items-center justify-center w-full sm:w-auto bg-slate-900 text-slate-300 border border-slate-800 px-8 py-4 rounded-full font-medium hover:bg-slate-800 hover:text-white transition-all duration-300 active:scale-95"
          >
            Explore features
          </a>
        </div>
      </main>

      {/* Features Grid */}
      <section id="features" className="container mx-auto px-6 py-24 border-t border-slate-800/50 relative z-10">
        <div className="grid md:grid-cols-3 gap-8 max-w-5xl mx-auto">
          {[
            {
              icon: BarChart3,
              title: 'Dynamic Analytics',
              description: 'Visualize your spending patterns instantly with interactive, highly responsive charts.',
              color: 'text-cyan-400',
              bg: 'bg-cyan-400/10'
            },
            {
              icon: Wallet,
              title: 'Advanced Filtering',
              description: 'Slice and dice your transaction data with powerful server-side specification filters.',
              color: 'text-indigo-400',
              bg: 'bg-indigo-400/10'
            },
            {
              icon: ShieldCheck,
              title: 'Bank-Grade Security',
              description: 'Your data is protected with state-of-the-art JWT authentication and encryption.',
              color: 'text-purple-400',
              bg: 'bg-purple-400/10'
            }
          ].map((feature, i) => (
            <div key={i} className="group p-8 rounded-3xl bg-slate-900/50 border border-slate-800/50 hover:bg-slate-800/50 transition-colors duration-500 backdrop-blur-sm">
              <div className={`w-12 h-12 rounded-2xl ${feature.bg} ${feature.color} flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-500`}>
                <feature.icon className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-semibold mb-3 text-slate-200">{feature.title}</h3>
              <p className="text-slate-400 leading-relaxed">{feature.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Custom Styles for animations */}
      <style dangerouslySetInnerHTML={{__html: `
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in-up {
          opacity: 0;
          animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
        }
      `}} />
    </div>
  );
}
